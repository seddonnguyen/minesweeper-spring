'use strict';

class Animation {
    constructor() {
        const count = 30;
        this.propCount = 8;
        this.propsLength = count * this.propCount;
        this.tick = 0;
        this.center = [];

        this.canvas = {
            a: document.createElement('canvas'),
            b: document.createElement('canvas')
        };

        this.ctx = {
            a: this.canvas.a.getContext('2d'),
            b: this.canvas.b.getContext('2d')
        };
    }


    rand = (n) => n * Math.random();

    fadeInOut = (t, m) => {
        let hm = 0.5 * m;
        return Math.abs((t + hm) % m - hm) / (hm);
    };

    setup() {
        this.createCanvas();
        this.resize();
        this.initPipes();
        this.draw();
    }

    createCanvas() {
        this.container = document.querySelector('.container');
        this.canvas.b.style = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
        `;
        this.container.appendChild(this.canvas.b);
    }

    deleteCanvas() {
        this.container = document.getElementsByTagName('canvas');
        this.container[0].remove();
    }

    resize() {
        const {innerWidth, innerHeight} = window;

        this.canvas.a.width = innerWidth;
        this.canvas.a.height = innerHeight;

        this.ctx.a.drawImage(this.canvas.b, 0, 0);

        this.canvas.b.width = innerWidth;
        this.canvas.b.height = innerHeight;

        this.ctx.b.drawImage(this.canvas.a, 0, 0);

        this.center[0] = 0.5 * this.canvas.a.width;
        this.center[1] = 0.5 * this.canvas.a.height;
    }

    initPipes() {
        this.pipeProps = new Float32Array(this.propsLength);

        for (let i = 0; i < this.propsLength; i += this.propCount) {
            this.initialize(i);
        }
    }

    initialize(i) {
        const HALF_PI = 0.5 * Math.PI;
        const TAU = 2 * Math.PI;
        const baseSpeed = 0.5;
        const rangeSpeed = 1;
        const baseTTL = 100;
        const rangeTTL = 300;
        const baseWidth = 2;
        const rangeWidth = 4;
        const baseHue = 39;
        const rangeHue = 60;

        let x = this.rand(this.canvas.a.width);
        let y = this.center[1];
        let direction = (Math.round(this.rand(1)) ? HALF_PI : TAU - HALF_PI);
        let speed = baseSpeed + this.rand(rangeSpeed);
        let life = 0;
        let ttl = baseTTL + this.rand(rangeTTL);
        let width = baseWidth + this.rand(rangeWidth);
        let hue = baseHue + this.rand(rangeHue);

        this.pipeProps.set([x, y, direction, speed, life, ttl, width, hue], i);
    }

    updateLines() {
        this.tick++;

        for (let i = 0; i < this.propsLength; i += this.propCount) {
            this.updateLine(i);
        }
    }

    updateLine(i) {
        const turnCount = 8;
        const turnAmount = (360 / turnCount) * Math.PI / 180;
        const turnChanceRange = 100;

        let i2 = 1 + i,
            i3 = 2 + i,
            i4 = 3 + i,
            i5 = 4 + i,
            i6 = 5 + i,
            i7 = 6 + i,
            i8 = 7 + i;

        let x = this.pipeProps[i];
        let y = this.pipeProps[i2];
        let direction = this.pipeProps[i3];
        let speed = this.pipeProps[i4];
        let life = this.pipeProps[i5];
        let ttl = this.pipeProps[i6]
        let width = this.pipeProps[i7];
        let hue = this.pipeProps[i8];

        this.drawLine(x, y, life, ttl, width, hue);
        x += Math.cos(direction) * speed;
        y += Math.sin(direction) * speed;
        [x, y] = this.checkBounds(x, y);

        let turnChance = !(this.tick % Math.round(this.rand(turnChanceRange))) && (!(Math.round(x) % 6) || !(Math.round(y) % 6));
        let turnBias = Math.round(this.rand(1)) ? -1 : 1;

        direction += turnChance ? turnAmount * turnBias : 0;
        life++;

        this.pipeProps[i] = x;
        this.pipeProps[i2] = y;
        this.pipeProps[i3] = direction;
        this.pipeProps[i5] = life;

        life > ttl && this.initialize(i);
    }

    drawLine(x, y, life, ttl, width, hue) {
        this.ctx.a.save();
        this.ctx.a.strokeStyle = `hsla(${hue},75%,50%,${this.fadeInOut(life, ttl) * 0.125})`;
        this.ctx.a.beginPath();
        this.ctx.a.arc(x, y, width, 0, 2 * Math.PI);
        this.ctx.a.stroke();
        this.ctx.a.closePath();
        this.ctx.a.restore();
    }

    checkBounds(x, y) {
        if (x > this.canvas.a.width) {
            x = 0;
        }

        if (x < 0) {
            x = this.canvas.a.width;
        }

        if (y > this.canvas.a.height) {
            y = 0;
        }

        if (y < 0) {
            y = this.canvas.a.height;
        }

        return [x, y];
    }

    render() {
        this.ctx.b.save();
        this.ctx.b.fillStyle = 'hsla(150,80%,1%,1)';
        this.ctx.b.fillRect(0, 0, this.canvas.b.width, this.canvas.b.height);
        this.ctx.b.restore();

        this.ctx.b.save();
        this.ctx.b.filter = 'blur(12px)'
        this.ctx.b.drawImage(this.canvas.a, 0, 0);
        this.ctx.b.restore();

        this.ctx.b.save();
        this.ctx.b.drawImage(this.canvas.a, 0, 0);
        this.ctx.b.restore();
    }

    draw() {
        this.updateLines();
        this.render();
        window.requestAnimationFrame(this.draw.bind(this));
    }
}

window.addEventListener('DOMContentLoaded', () => {
    const loadGameButton = document.getElementById('loadGameButton');
    const newGameButton = document.getElementById('newGameButton');
    const pipeAnimation = new Animation();
    pipeAnimation.createCanvas();
    pipeAnimation.setup();

    function redirect(eventButton, buttonText, path) {
        try {
            eventButton.disabled = true;
            eventButton.textContent = 'Loading...';
            window.location.replace(path);
        } catch (error) {
            console.error('Error redirecting:', error);
        } finally {
            eventButton.disabled = false;
            eventButton.textContent = buttonText;
        }
    }

    loadGameButton.addEventListener('click', () => {
        redirect(loadGameButton, 'Load Game', '/game');
    });

    newGameButton.addEventListener('click', () => {
        redirect(newGameButton, 'New Game', '/game/');
    });
});

window.addEventListener('resize', () => {
    const pipeAnimation = new Animation();
    pipeAnimation.deleteCanvas();
    pipeAnimation.createCanvas();
    pipeAnimation.setup();
});