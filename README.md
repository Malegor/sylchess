
# sylchess

This project is a Java-based chess program that allows users to play starting from a given position, or from the classical starting positions. The program can also solve puzzles up to 5 or 6 moves.


## Authors

- [@Malegor](https://github.com/Malegor)


## 🔗 Links
- [![github](https://img.shields.io/badge/github-repo-blue?logo=github)](https://github.com/Malegor/sylchess)
- [![jira](https://img.shields.io/badge/jira-0A66C2?style=for-the-badge&logo=jira&logoColor=white)](https://sylv1fournier.atlassian.net/jira/software/projects/SYLCHESS/boards/1)


## Features

- Chess game rules (including pawn promotion, en-passant, castling, etc.)
- Interactive game on console (human vs. human or possibility to call a solver to solve a puzzle, or to defend as best as possible)
- Solver based on an alpha-beta minimax algorithm
- Interactive game on desktop-based interface
- Load a given position from [FEN](https://www.chess.com/terms/fen-chess) description

## Running locally

Get the project jar. (OBS: with this command, the project is also available as a library for other projects).
```bash
  mvn clean install
```

Get the jar in `<home>/.m2/repository/com/sylvain/chess/sylchess/<version>/sylchess-<version>.jar`

TODO:
- java -jar sylchess.jar
- java -cp <whatever>

## Swing interface
See project [Swing Interface](https://github.com/Malegor/sylchess-swing)

## Web interface
See project [Web Interface](https://github.com/Malegor/sylchess-webserver)

## Future work

- consider clock for each player
- accept other pieces names (ex: distinct languages)
- generalize rules: ex. two or more kings
- generalize rules: several players for same color (test it)
- first phase before game: player chooses the positions of each piece
- improve Alpha-Beta's player performance

## Acknowledgements

 - [readme.so](https://readme.so/editor)
 - [Badges](https://shields.io/badges)

