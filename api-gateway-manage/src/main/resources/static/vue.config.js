const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    open: false,
    port: 8080,
    proxy: {
      ['/dev']: {
        target: 'http://localhost:30139',
        changeOrigin: true,
        secure: false,
        pathRewrite: { '^/dev': '' },
      }
    }
  },
  outputDir: 'ui-gw',
  publicPath: './',
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        Buffer: ['buffer', 'Buffer'],
      }),
    ],
    resolve: {
      fallback: {
        "path": require.resolve("path-browserify"),
        "https": require.resolve("https-browserify"),
        "http": require.resolve("stream-http"),
        "buffer": require.resolve("buffer"),
        fs: false
      },
    },
  }
})
