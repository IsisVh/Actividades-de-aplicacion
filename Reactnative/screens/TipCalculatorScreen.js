import React, { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

export default function TipCalculatorScreen() {
  const [total, setTotal] = useState('');
  const [porcentaje, setPorcentaje] = useState('');
  const [resultado, setResultado] = useState(null);

  const calcularPropina = () => {
    const totalNum = parseFloat(total);
    const porcentajeNum = parseFloat(porcentaje);
    const propina = totalNum * (porcentajeNum / 100);
    setResultado(propina.toFixed(2));
  };

  return (
    <View style={styles.container}>
      <TextInput
        placeholder="Total de la cuenta"
        keyboardType="numeric"
        style={styles.input}
        value={total}
        onChangeText={setTotal}
      />
      <TextInput
        placeholder="Porcentaje de propina"
        keyboardType="numeric"
        style={styles.input}
        value={porcentaje}
        onChangeText={setPorcentaje}
      />
      <Button title="Calcular Propina" onPress={calcularPropina} />
      {resultado && <Text style={styles.result}>Propina: ${resultado}</Text>}
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
