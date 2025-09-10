import React, { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

export default function IMCScreen() {
  const [peso, setPeso] = useState('');
  const [altura, setAltura] = useState('');
  const [resultado, setResultado] = useState(null);

  const calcularIMC = () => {
    const pesoNum = parseFloat(peso);
    const alturaNum = parseFloat(altura) / 100;
    const imc = pesoNum / (alturaNum * alturaNum);
    setResultado(imc.toFixed(2));
  };

  return (
    <View style={styles.container}>
      <TextInput
        placeholder="Peso (kg)"
        keyboardType="numeric"
        style={styles.input}
        value={peso}
        onChangeText={setPeso}
      />
      <TextInput
        placeholder="Altura (cm)"
        keyboardType="numeric"
        style={styles.input}
        value={altura}
        onChangeText={setAltura}
      />
      <Button title="Calcular IMC" onPress={calcularIMC} />
      {resultado && <Text style={styles.result}>Tu IMC es: {resultado}</Text>}
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
