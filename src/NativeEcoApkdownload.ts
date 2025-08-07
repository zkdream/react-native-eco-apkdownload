/*
 * @Author: 星光 1558471295@qq.com
 * @Date: 2025-08-06 17:19:46
 * @FilePath: \react-native-eco-apkdownload\src\NativeEcoApkdownload.ts
 * @Description: 
 * Copyright (c) 2025 by 星光, All Rights Reserved. 
 */
import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  downloadApk(path: string, name: string): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('EcoApkdownload');
