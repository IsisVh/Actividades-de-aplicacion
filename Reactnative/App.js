import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import HomeScreen from './screens/HomeScreen';
import IMCScreen from './screens/IMCScreen';
import CurrencyConverterScreen from './screens/CurrencyConverterScreen';
import TipCalculatorScreen from './screens/TipCalculatorScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="IMC" component={IMCScreen} />
        <Stack.Screen name="Divisas" component={CurrencyConverterScreen} />
        <Stack.Screen name="Propinas" component={TipCalculatorScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
