/*
 * @Author: 星光 1558471295@qq.com
 * @Date: 2025-08-06 17:19:46
 * @FilePath: \react-native-eco-apkdownload\src\index.tsx
 * @Description: 
 * Copyright (c) 2025 by 星光, All Rights Reserved. 
 */
import { Platform } from 'react-native';
import EcoApkdownload from './NativeEcoApkdownload';

export function downloadApk(path: string, name: string): void {
  if (Platform.OS == 'android') {
    EcoApkdownload.downloadApk(path, name);
  }
}
