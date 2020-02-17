const fs = require('fs-extra')

fs.removeSync('platforms/android/app/src/main/assets/dist')
fs.copySync('dist', 'platforms/android/app/src/main/assets')
