# How to Use Zoom SDK in JavaScript

The instructions for using the SDK in JavaScript are similar to [JVM](usage-jvm.md).
You can use the SDK in a Node.js environment or in the browser.

## SDK Initialization

In Node.js, you can import the SDK as follows:

```javascript
const {ZoomJs} = require('path/to/zoomsdk-wrapper')
const zoom = new ZoomJs("clientId", "clientSecret");
```

The `zoomsdk-wrapper` is a UMD module that can be used in Node.js or the browser.
It is automatically built when you run the `build` task and is located in the `build/js/packages/zoomsdk/kotlin/` directory.

To use it in the browser, you can include the SDK as a script tag:

```html
<script src="path/to/zoomsdk-wrapper.js"></script>
<script>
  const zoom = new ZoomJs("clientId", "clientSecret");
</script>
```

## Blocking vs Non-Blocking Calls in JavaScript
In JavaScript, all SDK calls are asynchronous and non-blocking by default.
They return a `Promise` that you can `await` or use `.then()` to handle the result.
