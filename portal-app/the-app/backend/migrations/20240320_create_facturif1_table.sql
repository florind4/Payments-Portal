CREATE TABLE IF NOT EXISTS facturif1 (
    id SERIAL PRIMARY KEY,
    numar_factura VARCHAR(50) NOT NULL,
    data_emitere DATE NOT NULL,
    data_scadenta DATE NOT NULL,
    valoare_totala DECIMAL(10,2) NOT NULL,
    valoare_tva DECIMAL(10,2) NOT NULL,
    valoare_net DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    cod_client VARCHAR(50) NOT NULL,
    adresa_client TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
); 