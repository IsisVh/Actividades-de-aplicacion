import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
//import { UseStateDemo } from './components';
//import { UseEffectDemo } from './components';
import { UseMemoDemo } from './components';
export default function App() {
  return (
    <View style={styles.container}>
      <UseMemoDemo/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});