import React, { useMemo } from 'react';
import { Text } from 'react-native';

const users = [
  { name: 'Juan', age: 23 },
  { name: 'Elisa', age: 40 }
];

export default () => {
  const totalAge = useMemo(() => {
    let total = 0;
    console.log('Calculando sumatoria de edad...');
    users.forEach(x => {
      total = total + x.age;
    });
    return total;
  }, [users]);

  return (
    <Text>{totalAge}</Text>
  );
};