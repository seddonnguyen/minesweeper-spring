# Minesweeper Spring Boot Project

Welcome to the Minesweeper Spring Boot project! This repository contains a simple implementation of the classic Minesweeper game using Spring Boot. Below you'll find all the necessary details to get started.

## Table of Contents
- [About](#about)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [License](#license)

## About
This project is a Spring Boot application that simulates the classic Minesweeper game. It is designed for educational purposes, showcasing how to create a simple game using Java and Spring Boot.

## Features
- Interactive Minesweeper game
- RESTful API for game operations
- Simple and clean code structure
- Easy to deploy and run
- You have 3 lives to play before you lose the game
- Ability to play saved games

## Installation

### Prerequisites
- Java 11 or higher
- Maven

### Steps
1. Clone the repository:
    ```sh
    git clone https://github.com/seddonnguyen/minesweeper-spring.git
    ```
2. Navigate to the project directory:
    ```sh
    cd minesweeper-spring
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Usage
1. Run the application:
    ```sh
    mvn spring-boot:run
    ```
2. Access the game at `http://localhost:8080`.

### How to Play
- **Left click:** Reveal a cell
- **Right click:** Add/remove a flag
- **Lives:** You have 3 lives before you lose the game
- **Saved Games:** Ability to play saved games

### DEMO

![](minesweeper-demo.gif)