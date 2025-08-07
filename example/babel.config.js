/*
 * @Author: 星光 1558471295@qq.com
 * @Date: 2025-08-06 17:19:45
 * @FilePath: \react-native-eco-apkdownload\example\babel.config.js
 * @Description: 
 * Copyright (c) 2025 by 星光, All Rights Reserved. 
 */
const path = require('path');
const { getConfig } = require('react-native-builder-bob/babel-config');
const pkg = require('../package.json');

const root = path.resolve(__dirname, '..');

module.exports = getConfig(
  {
    presets: ['module:@react-native/babel-preset'],
  },
  { root, pkg }
);
