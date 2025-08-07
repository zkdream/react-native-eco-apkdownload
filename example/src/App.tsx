/*
 * @Author: 星光 1558471295@qq.com
 * @Date: 2025-08-06 17:19:46
 * @FilePath: \react-native-eco-apkdownload\example\src\App.tsx
 * @Description: 
 * Copyright (c) 2025 by 星光, All Rights Reserved. 
 */
import { Text, View, StyleSheet } from 'react-native';
import { downloadApk } from '../../src/index';


export default function App() {
  return (
    <View style={styles.container}>
      <Text
        style={{
          backgroundColor: 'red',
          width: 100,
          height: 100,
          color: 'white',
          fontSize: 16,
          textAlign:'center',
          textAlignVertical:'center'

        }}
        onPress={() =>{
             downloadApk('https://ceshiapk.ecosteam.cn/ext/ECOSteam.apk?comment=TH0001&auth_key=1754556625-0-0-c97ac136aa57feaa82e2e2f0ef617a06', 'ECOSteam'+Date.now())
             console.log('5555');
        }}>
            5555</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
