<template>
    <v-card class="mx-auto" background-color="#222">
        <div class="panel">
            <div class="scanner"></div>
            <ul class="devices">
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
            </ul>
        </div>
    </v-card>
</template>

<script>
export default {}
</script>

<style lang="scss" scoped>
.panel {
    position: relative;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    box-shadow: inset 0 0 0 99px #222, inset 0 0 0 100px rgba(0, 255, 0, 0.2), inset 0 0 0 199px #222,
        inset 0 0 0 200px rgba(0, 255, 0, 0.2), inset 0 0 0 299px #222, inset 0 0 0 300px rgba(0, 255, 0, 0.2);
    border-radius: 50% !important;
    transform: rotate(0deg);
    overflow: hidden;
    width: 400px;
    height: 400px;
    border: 1px solid #393;
}
.panel .scanner {
    animation: scanning 6s infinite linear;
    background-image: url('data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4gPHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJncmFkIiBncmFkaWVudFVuaXRzPSJvYmplY3RCb3VuZGluZ0JveCIgeDE9IjAuMCIgeTE9IjEuMCIgeDI9IjEuMCIgeTI9IjAuMCI+PHN0b3Agb2Zmc2V0PSIwJSIgc3RvcC1jb2xvcj0iIzAwZmYwMCIvPjxzdG9wIG9mZnNldD0iNTAlIiBzdG9wLWNvbG9yPSIjMDAwMDAwIiBzdG9wLW9wYWNpdHk9IjAuMCIvPjwvbGluZWFyR3JhZGllbnQ+PC9kZWZzPjxyZWN0IHg9IjAiIHk9IjAiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JhZCkiIC8+PC9zdmc+IA==');
    background-size: 100%;
    background-image: linear-gradient(to top right, #00ff00 0%, rgba(0, 0, 0, 0) 50%);
    transform-origin: top left;
    position: absolute;
    top: 50%;
    left: 50%;
    width: 400px;
    height: 400px;
    border-left: 1px solid rgba(0, 255, 0, 0.1);
}
.panel:before {
    content: '';
    display: block;
    position: absolute;
    top: 0;
    left: 50%;
    width: 1px;
    height: 100%;
    background: rgba(0, 255, 0, 0.2);
}
.panel:after {
    content: '';
    display: block;
    position: absolute;
    top: 50%;
    left: 0;
    width: 100%;
    height: 1px;
    background: rgba(0, 255, 0, 0.2);
}
.panel .devices {
    list-style: none;
}
.panel .devices li {
    animation: target 6s infinite linear;
    border-radius: 50%;
    box-shadow: 0 0 10px #afa;
    opacity: 0.75;
    display: block;
    position: absolute;
    width: 10px;
    height: 10px;
    background: #afa;
}
.panel .devices li:nth-child(1) {
    animation-delay: 2250ms;
    top: 30%;
    left: 30%;
}
.panel .devices li:nth-child(2) {
    animation-delay: 3200ms;
    top: 15%;
    left: 60%;
}
.panel .devices li:nth-child(3) {
    animation-delay: 5050ms;
    top: 70%;
    left: 80%;
}
.panel .devices li:nth-child(4) {
    animation-delay: 1100ms;
    top: 60%;
    left: 20%;
}
.panel .devices li:nth-child(5) {
    animation-delay: 3000ms;
    top: 30%;
    left: 50%;
}

@keyframes scanning {
    100% {
        transform: rotate(360deg);
    }
}

@keyframes target {
    1% {
        opacity: 0.75;
        transform: scale(2);
    }
    3% {
        opacity: 1;
        transform: scale(1.5);
    }
    60% {
        opacity: 0.75;
        transform: scale(0.75);
    }
}
</style>
