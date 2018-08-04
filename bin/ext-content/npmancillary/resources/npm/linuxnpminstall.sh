BASEDIR=$(dirname "$0")
export PATH=$BASEDIR/node/node-v6.9.4-linux-x64/bin:$PATH
rm -rf $BASEDIR/node_modules
cd $BASEDIR
npm cache clean
npm install