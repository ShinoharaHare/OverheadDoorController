<template>
    <div :class="divClass" @click="$emit('click', $event)">
        <button class="js-start-test test-mode-multi">
            <span class="start-ring"></span>
            <span class="start-background"></span>
            <span class="start-border"></span>
            <span class="start-text">
                <v-icon color="white" size="0.8em" :class="iconClass">
                    {{ iconMap[state] }}
                </v-icon>
            </span>
        </button>
    </div>
</template>

<script>
import { mapState } from 'vuex'

import { ConnectionState } from '@/types'

export default {
    name: 'ConnectButton',
    data: () => ({
        iconMap: {
            [ConnectionState.DISCONNECTED]: 'mdi-bluetooth',
            [ConnectionState.CONNECTING]: 'mdi-bluetooth-audio',
            [ConnectionState.CONNECTED]: 'mdi-bluetooth-connect'
        }
    }),
    computed: {
        ...mapState('bluetooth', ['state']),
        ...mapState('setting', ['device', 'pin']),
        iconClass() {
            switch (this.state) {
                case ConnectionState.DISCONNECTED:
                    return ''
                case ConnectionState.CONNECTING:
                    return 'animated infinite heartBeat'
                case ConnectionState.CONNECTED:
                    return ''
                default:
                    return ''
            }
        },
        divClass() {
            switch (this.state) {
                case ConnectionState.DISCONNECTED:
                    return 'start-button'
                case ConnectionState.CONNECTING:
                    return 'start-button connecting'
                case ConnectionState.CONNECTED:
                    return 'start-button'
                default:
                    return ''
            }
        }
    },
    methods: {},
    mounted() {}
}
</script>

<style lang="scss" scoped>
.connecting {
    transform: scale(0.9);
    transition-duration: 0.3s;

    .start-border,
    .start-ring {
        animation-name: none !important;
    }
}

*,
:after,
:before {
    box-sizing: inherit;
}
button {
    cursor: pointer;
    color: #1cbfff;
    text-decoration: none;
    outline: 0;
}
button:active,
button:focus,
button:hover {
    text-decoration: none;
    outline: 0;
    color: #6afff3;
}
.start-button {
    text-align: center;
    white-space: nowrap;
    margin: 0;

    * {
        pointer-events: none;
    }
}
.start-button button {
    z-index: 1;
    color: #fff;
    text-transform: uppercase;
    text-decoration: none;
    display: inline-block;
    text-align: center;
    font-size: 40px;
    font-size: 4rem;
    font-weight: 500;
    box-sizing: border-box;
    position: relative;
    left: 0;
    padding: 0;
    width: 180px;
    height: 180px;
    line-height: 180px;
    border-radius: 180px;
    outline: 0;
    transform: translateY(20px) translateX(0);
    transform-origin: center center;
    border-width: 0;
}
.start-button button .start-text {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 180px;
    height: 180px;
    border-radius: 180px;
    box-sizing: border-box;
    white-space: nowrap;
    opacity: 1;
}
.start-button button .start-border {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 180px;
    height: 180px;
    border-radius: 180px;
    box-sizing: border-box;
    background-color: #141526;
    border: 2px transparent solid;
    background-origin: border-box;
    background-clip: content-box, border-box;
    background-image: linear-gradient(#141526, #141526), linear-gradient(to bottom, #2de5d1, #1fa4e9);
    animation-name: start-heartbeat;
    animation-delay: 0s;
    animation-duration: 3.5s;
    animation-iteration-count: infinite;
    animation-timing-function: ease-out;
}
.start-button button .start-background {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 180px;
    height: 180px;
    border-radius: 180px;
    box-sizing: border-box;
    opacity: 0;
    background-color: #232f4e;
}
.start-button button .start-ring {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 180px;
    height: 180px;
    border-radius: 180px;
    box-sizing: border-box;
    border: 2px #26c5dd solid;
    opacity: 0;
    animation-name: start-ring;
    animation-delay: 0s;
    animation-duration: 3.5s;
    animation-iteration-count: infinite;
    animation-timing-function: linear;
}
.start-button button:focus .start-border,
.start-button button:hover .start-border {
    background-image: linear-gradient(rgba(20, 21, 38, 0.95), rgba(20, 21, 38, 0.95)),
        linear-gradient(to bottom, #2de5d1, #1fa4e9);
    animation-name: none;
}
.start-button button:focus .start-ring,
.start-button button:hover .start-ring {
    animation-name: none;
}
/*! CSS Used keyframes */
@keyframes start-heartbeat {
    0% {
        transform: scale(1);
    }
    8.333% {
        transform: scale(0.989);
    }
    16.667% {
        transform: scale(1);
    }
}
@keyframes start-ring {
    0% {
        opacity: 0;
        transform: scale(1);
    }
    12.5% {
        opacity: 0;
        transform: scale(0.995);
    }
    16.667% {
        opacity: 1;
    }
    50% {
        opacity: 0;
        transform: scale(1.3);
    }
}
</style>
