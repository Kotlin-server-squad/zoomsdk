const {createServer} = require('node:http');

const hostname = '127.0.0.1';
const port = 3000;

const {ZoomJs} = require('../../../build/js/packages/zoomsdk/kotlin/zoomsdk-wrapper')
const zoom = new ZoomJs("client-id", "client-secret");
console.log(zoom.auth())

const server = createServer((req, res) => {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/plain');
    res.end('Hello World');
});

server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);
});
