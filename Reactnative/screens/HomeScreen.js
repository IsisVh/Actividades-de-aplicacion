import React from 'react';
import { View, Button, StyleSheet } from 'react-native';

export default function HomeScreen({ navigation }) {
  return (
    <View style={styles.container}>
      <Button title="Calcular IMC" onPress={() => navigation.navigate('IMC')} />
      <Button title="Cambio de Divisas" onPress={() => navigation.navigate('Divisas')} />
      <Button title="Calcular Propinas" onPress={() => navigation.navigate('Propinas')} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'space-evenly',
    padding: 20,
  },
});
