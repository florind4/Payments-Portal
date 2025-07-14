import { Pool } from 'pg';
import bcrypt from 'bcrypt';
import crypto from 'crypto';
import jwt from 'jsonwebtoken';
import { JWT_SECRET } from '../config';

const pool = new Pool();

export interface Developer {
  id: number;
  name: string;
  email: string;
  company: string;
  job: string;
  phone: string;
  country?: string;
  address?: string;
  url?: string;
  secret: string;
}

export const registerDeveloper = async (data: {
  name: string;
  email: string;
  company: string;
  job: string;
  password: string;
  phone: string;
  country?: string;
  address?: string;
  url?: string;
}): Promise<Developer> => {
  const client = await pool.connect();

  try {

    const emailCheck = await client.query(
      'SELECT id FROM developers WHERE email = $1',
      [data.email]
    );

    if (emailCheck.rows.length > 0) {
      throw new Error('Email already registered');
    }


    const secret = crypto.randomBytes(10).toString('hex');


    const hashedPassword = await bcrypt.hash(data.password, 10);


    const result = await client.query(
      `INSERT INTO developers (
        name, email, company, job, password, phone, country, address, url, secret
      ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
      RETURNING id, name, email, company, job, phone, country, address, url, secret`,
      [
        data.name,
        data.email,
        data.company,
        data.job,
        hashedPassword,
        data.phone,
        data.country,
        data.address,
        data.url,
        secret,
      ]
    );

    return result.rows[0];
  } finally {
    client.release();
  }
};

export const loginDeveloper = async (email: string, password: string): Promise<{ token: string; secret: string }> => {
  const client = await pool.connect();

  try {

    const result = await client.query(
      'SELECT id, email, password, secret FROM developers WHERE email = $1',
      [email]
    );

    if (result.rows.length === 0) {
      throw new Error('Invalid credentials');
    }

    const developer = result.rows[0];

    const isValidPassword = await bcrypt.compare(password, developer.password);
    if (!isValidPassword) {
      throw new Error('Invalid credentials');
    }

    const token = jwt.sign(
      { 
        id: developer.id,
        email: developer.email,
        type: 'developer'
      },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    return {
      token,
      secret: developer.secret
    };
  } finally {
    client.release();
  }
}; 