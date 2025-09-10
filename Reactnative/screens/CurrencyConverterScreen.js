import React, { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

export default function CurrencyConverterScreen() {
  const [cantidad, setCantidad] = useState('');
  const [resultado, setResultado] = useState(null);
  const tasaCambio = 0.85; // 1 USD = 0.85 EUR

  const convertir = () => {
    const cantidadNum = parseFloat(cantidad);
    const convertido = cantidadNum * tasaCambio;
    setResultado(convertido.toFixed(2));
  };

  return (
    <View style={styles.container}>
      <TextInput
        placeholder="Cantidad en USD"
        keyboardType="numeric"
        style={styles.input}
        value={cantidad}
        onChangeText={setCantidad}
      />
      <Button title="Convertir a EUR" onPress={convertir} />
      {resultado && <Text style={styles.result}>EUR: €{resultado}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  input: {
    borderWidth: 1,
    padding: 10,
    marginVertical: 10,
    borderRadius: 5,
  },
  result: {
    marginTop: 20,
    fontSize: 18,
    fontWeight: 'bold',
  },
});
