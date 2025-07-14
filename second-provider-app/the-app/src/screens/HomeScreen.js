import React, { useState, useEffect, useCallback, useRef } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, RefreshControl } from 'react-native';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { getFormattedAmount } from '../utils/currencyConverter';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_URL } from '../src/config';

const HomeScreen = () => {
  const navigation = useNavigation();
  const [userCurrency, setUserCurrency] = useState('RON');
  const [refreshing, setRefreshing] = useState(false);
  const [user, setUser] = useState(null);
  const [bills, setBills] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const isMounted = useRef(true);
  const [isAdmin, setIsAdmin] = useState(false);

  const checkAdminStatus = async () => {
    try {
      const token = await AsyncStorage.getItem('userToken');
      if (!token) return false;

      const response = await fetch(`${API_URL}/users/admin-status`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) return false;

      const data = await response.json();
      return data.admin === 'DA';
    } catch (error) {
      console.error('Error checking admin status:', error);
      return false;
    }
  };

  const fetchData = useCallback(async () => {
    if (!isMounted.current) return;

    try {
      const token = await AsyncStorage.getItem('userToken');
      if (!token) {
        console.error('No token found');
        return;
      }

      const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Cache-Control': 'no-cache',
        'Pragma': 'no-cache'
      };

      // Check admin status first
      const adminStatus = await checkAdminStatus();
      if (isMounted.current) {
        setIsAdmin(adminStatus);
      }

      // Only fetch user data and bills if not admin
      if (!adminStatus) {
        // Fetch user data
        const userResponse = await fetch(`${API_URL}/users/balance`, {
          method: 'GET',
          headers
        });

        if (!userResponse.ok) {
          throw new Error('Failed to fetch user data');
        }

        const userData = await userResponse.json();
        if (isMounted.current) {
          setUser(userData);
        }

        // Fetch bills
        const billsResponse = await fetch(`${API_URL}/facturi`, {
          method: 'GET',
          headers
        });

        if (!billsResponse.ok) {
          throw new Error('Failed to fetch bills');
        }

        const billsData = await billsResponse.json();
        if (isMounted.current) {
          setBills(billsData);
        }

        // Fetch currency
        const currencyResponse = await fetch(`${API_URL}/users/currency`, {
          method: 'GET',
          headers
        });

        if (!currencyResponse.ok) {
          throw new Error('Failed to fetch currency');
        }

        const currencyData = await currencyResponse.json();
        if (isMounted.current && currencyData.currency) {
          setUserCurrency(currencyData.currency);
        }
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      if (isMounted.current) {
        setError(error.message);
      }
    }
  }, []);

  useEffect(() => {
    fetchData();
    return () => {
      isMounted.current = false;
    };
  }, [fetchData]);

  useFocusEffect(
    useCallback(() => {
      fetchData();
    }, [fetchData])
  );

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await fetchData();
    setRefreshing(false);
  }, [fetchData]);

  return (
    <View style={styles.container}>
      <ScrollView 
        refreshControl={
          <RefreshControl 
            refreshing={refreshing} 
            onRefresh={onRefresh}
            colors={['#4A90E2']}
            tintColor="#4A90E2"
          />
        }
      >
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Welcome, {user?.username || 'User'}</Text>
          {!isAdmin && user && (
            <Text style={styles.balanceText}>
              Balance: {getFormattedAmount(user?.balance || 0, userCurrency)}
            </Text>
          )}
        </View>
        
        <View style={styles.dashboardSection}>
          {isAdmin ? (
            <>
              <TouchableOpacity
                style={styles.adminButton}
                onPress={() => navigation.navigate('CreateBill')}
              >
                <Text style={styles.adminButtonText}>Create Bill</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={styles.adminButton}
                onPress={() => navigation.navigate('ManageUsers')}
              >
                <Text style={styles.adminButtonText}>Manage Users</Text>
              </TouchableOpacity>
            </>
          ) : (
            <>
              <TouchableOpacity
                style={styles.balanceButton}
                onPress={() => navigation.navigate('PaymentMethod')}
              >
                <Text style={styles.balanceButtonText}>
                  Balance: {getFormattedAmount(user?.balance || 0, userCurrency)}
                </Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.billsButton}
                onPress={() => navigation.navigate('Bills')}
              >
                <Text style={styles.billsButtonText}>
                  Bills ({bills.length})
                </Text>
              </TouchableOpacity>
            </>
          )}
        </View>
        
        {!isAdmin && bills && bills.length > 0 && bills.map((bill) => (
          <TouchableOpacity
            key={bill.id}
            style={styles.billCard}
            onPress={() => navigation.navigate('BillDetails', { billId: bill.id })}
          >
            <View style={styles.billHeader}>
              <Text style={styles.billTitle}>{bill.tip || bill.type}</Text>
              <Text style={styles.billAmount}>
                {getFormattedAmount(bill.sum, userCurrency)}
              </Text>
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    padding: 20,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  balanceText: {
    fontSize: 18,
  },
  dashboardSection: {
    padding: 20,
    gap: 10,
  },
  adminButton: {
    backgroundColor: '#4A90E2',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 10,
  },
  adminButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  balanceButton: {
    backgroundColor: '#4CAF50',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 10,
  },
  balanceButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  billsButton: {
    backgroundColor: '#FF9800',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 10,
  },
  billsButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  billCard: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
  billHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  billTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  billAmount: {
    fontSize: 16,
  },
});

export default HomeScreen; 