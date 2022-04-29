import React, {useEffect, useState, useRef} from 'react';
import {observeSteps, unobserveSteps, askPermissions, addListener} from 'react-native-onfoot';

import {
  StyleSheet,
  Text,
  View,
  Button, TextInput
} from 'react-native';

const App = () => {
  const [steps, setSteps] = useState('None');
  const [date, setDate] = useState(/*(new Date()).toISOString()*/ '2022-04-28T20:51:53.066Z');
  const [active, setActive] = useState(false);

  const listener = useRef(null);

  useEffect(() => {
    if (active) {
      observeSteps(date);
      listener.current = addListener(steps => setSteps(steps));
    } else {
      unobserveSteps();
      listener.current?.remove();
    }
  }, [setSteps, active]);

  return (
    <View style={styles.container}>
      <Text>Steps count: {steps}</Text>

      <View style={styles.row}>
        <Text>Start Date:</Text>
        <TextInput value={date} onChangeText={val => setDate(val)} />
      </View>

      <View style={styles.row}>
        <Text>Count active:</Text>
        <Button title={active ? 'ACTIVE':'INACTIVE'} onPress={() => setActive(!active)} />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 15,
    alignItems: 'center',
    justifyContent: 'center',
  },
  row: {
    flexDirection: 'row',
    margin: 10,
    alignItems: 'center'
  }
});

export default App;
