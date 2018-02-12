import React, { Component } from 'react';
import './App.css';
import SockJs from 'sockjs-client';
import Stomp from 'stompjs';
import axios from 'axios';
import { Rectangle } from 'react-shapes';

// There are two boards - shipBoard and attackBoard
// shipsSquares first board will be Orange - +1
// ownAttackedShips on first board will be red - -1
// opponentAttackedShips (target reached) on second board will be Green +1
// opponentOceanOnly (no target found) on second board will be DarkBlue - -1
// all other squares will be skyblue - 0

class App extends Component {
  constructor(props) {
    super(props)
    this.state = {
      online: false,
      messages: '',
      playerType: null,
      attackBoard: null,
      shipBoard: null,
      dataLoaded: false
    }
  }

  componentWillMount() {
    var ourPlayer;
    var herOpponent;
    axios.get('http://localhost:8080/is-there-registered-player')
    .then(response => {
      // player 1 or 2
      if (response.data === true) {
        ourPlayer = 'player2'
        herOpponent = 'player1'
        console.log('Registered as Player 2');
        axios.get(`http://localhost:8080/get-player-board?player=${ourPlayer}`)
        .then(response => {
          axios.get(`http://localhost:8080/get-player-board?player=${herOpponent}`)
          .then(secondResponse => {
            this.setState({
              playerType: ourPlayer,
              opponent: herOpponent,
              shipBoard: this.convertHashMapToArray(response.data.boardArray),
              attackBoard: this.convertHashMapToArray(response.data.attackBoardArray),
              opponentShipBoard: this.convertHashMapToArray(secondResponse.data.boardArray),
              opponentAttackBoard: this.convertHashMapToArray(secondResponse.data.attackBoardArray),
              dataLoaded: true
            })
          })
        })
        .catch(e => {
          console.log(e);
        })
      } else {
        ourPlayer = 'player1'
        herOpponent = 'player2'

        console.log('Registered as Player 1');
        axios.get(`http://localhost:8080/get-player-board?player=${ourPlayer}`)
        .then(response => {
          axios.get(`http://localhost:8080/get-player-board?player=${herOpponent}`)
          .then(secondResponse => {
            this.setState({
              playerType: ourPlayer,
              opponent: herOpponent,
              shipBoard: this.convertHashMapToArray(response.data.boardArray),
              attackBoard: this.convertHashMapToArray(response.data.attackBoardArray),
              opponentShipBoard: this.convertHashMapToArray(secondResponse.data.boardArray),
              opponentAttackBoard: this.convertHashMapToArray(secondResponse.data.attackBoardArray),
              dataLoaded: true
            })
          })
        })
        .catch(e => {
          console.log(e);
        })
      }
    })
    .catch(e => {
      console.log(e);
    })
  }

  convertHashMapToArray(board) {
    var finalBoardArray = Array(10).fill(0).map(x => Array(10).fill(0))
    for (let i = 0; i < finalBoardArray.length; i++) {
      for (let j = 0; j < finalBoardArray[i].length; j++) {
        finalBoardArray[i][j] = board[this.createElement(i, j)];
      }
    }
    return finalBoardArray;
  }

  createElement(row, column) {
    return (`{${row}=${column}}`);
  }

  render() {
    return (
      <div className="App">
        {this.state.dataLoaded
          ? <Board player={this.state.playerType} opponent={this.state.opponent} attackBoard={this.state.attackBoard} shipBoard={this.state.shipBoard} opponentShipBoard={this.state.opponentShipBoard} opponentAttackBoard={this.state.opponentAttackBoard}/>
          :  "Data Not Loaded Yet"
        }
      </div>
    );
  }
}

class Board extends Component {
  constructor(props) {
    super(props)
    this.state = {
      AllMessages: [],
      text: '',
      win: false,
      lost: false,
      gameEndState: false,
      rowKey: 0,
      colKey: 0,
      shipSunkCount: 0
    }
  }
  componentWillMount() {
    this.setState({
      player: this.props.player,
      opponent: this.props.opponent,
      shipBoard: this.props.shipBoard,
      attackBoard: this.props.attackBoard,
      opponentShipBoard: this.props.opponentShipBoard,
      opponentAttackBoard: this.props.opponentAttackBoard,
    })
  }

  componentDidMount() {
    var socket = new SockJs('http://localhost:8080/application-stomp-websocket-endpoint');
    var stompClient = Stomp.over(socket);
    this.setState({
      client: stompClient
    })
    let that = this
      stompClient.connect({}, function (frame) {
        console.log("Connecting...");
        stompClient.subscribe(`/topic/${that.state.opponent}`, function(messageReceived) {
          console.log(`${that.state.opponent} attacked you`, JSON.parse(messageReceived.body));
          let attackedSquare = JSON.parse(messageReceived.body);
          let playerShipBoard = that.state.shipBoard;
          if (playerShipBoard[attackedSquare.someRow][attackedSquare.someCol] === 1) {
            playerShipBoard[attackedSquare.someRow][attackedSquare.someCol] = -1;
            that.setState({
              shipBoard: playerShipBoard
            })
          } else {
            playerShipBoard[attackedSquare.someRow][attackedSquare.someCol] = -2;
            that.setState({
              shipBoard: playerShipBoard
            })
          }
        })
        stompClient.subscribe(`/topic/${that.state.opponent}-message`, function(messageReceived) {
          console.log(`${that.state.opponent} sent you a message`, JSON.parse(messageReceived.body));
          var allMessages = that.state.AllMessages;
          allMessages.push({playerType: 'YourNemesis', message: messageReceived.body});
          that.setState({
            AllMessages: allMessages
          })
        })
        stompClient.subscribe(`/topic/${that.state.opponent}-has-won`, function(messageReceived) {
          console.log(`${that.state.opponent} has won`, JSON.parse(messageReceived.body));
          that.setState({
            gameEndState: true,
            lost: true
          })
        })
      })
  }

  handleAttackClick(someRow, someCol) {
    let playerAttackBoard = this.state.attackBoard;
    let opponentShipBoard = this.state.opponentShipBoard;
    if (opponentShipBoard[someRow][someCol] === 1) {
      if (this.state.shipSunkCount === 14) {
        var player = this.state.player
        this.state.client.send(`/app/${this.state.player}-win`, {}, JSON.stringify({player}));
        this.setState({
          attackBoard: playerAttackBoard,
          win: true,
          gameEndState: true
        })
      } else {
        this.state.client.send(`/app/sent-from-${this.state.player}`, {}, JSON.stringify({someRow, someCol}));
        playerAttackBoard[someRow][someCol] = 1;
        this.setState({
          attackBoard: playerAttackBoard,
          shipSunkCount: this.state.shipSunkCount + 1
        })
      }
    } else {
      this.state.client.send(`/app/sent-from-${this.state.player}`, {}, JSON.stringify({someRow, someCol}));
      playerAttackBoard[someRow][someCol] = -1;
      this.setState({
        attackBoard: playerAttackBoard
      })
    }
  }

  handleShipClick(someRow, someCol) {
    console.log(someRow, someCol);
  }

  handleColor(colorItem, boardType) {
    if (boardType === 'shipBoard') {
      if (colorItem === 1) {
        return 'orange'; // ship
      } else if (colorItem === -1) { // ship attacked
        return 'red'
      } else if (colorItem === -2) {
        return 'grey'; // space attacked but nothing there
      } else {
        return 'SkyBlue'; // just ocean
      }
    } else {
      if (colorItem === 1) {
        return 'green'
      } else if (colorItem === -1) {
        return 'Navy'
      } else {
        return 'SkyBlue'
      }
    }
  }

  handleTextChange(e) {
    this.setState({
      text: e.target.value
    })
  }

  handleMessageSend() {
    var allMessages = this.state.AllMessages;
    allMessages.push({playerType: 'ourPlayer', message: this.state.text});
    this.setState({
      text: '',
      AllMessages: allMessages
    })
    this.state.client.send(`/app/message-from-${this.state.player}`, {}, JSON.stringify(this.state.text));
  }

  showMessages() {
    return(
      this.state.AllMessages.map(message => {
        return <Message player={message.playerType} message={message.message}/>
      })
    )
  }

  handleKeyPress(e){
    if (e.key === 'Enter') {
      var allMessages = this.state.AllMessages;
      allMessages.push({playerType: 'ourPlayer', message: this.state.text});
      this.setState({
        text: '',
        AllMessages: allMessages
      })
      this.state.client.send(`/app/message-from-${this.state.player}`, {}, JSON.stringify(this.state.text));
    }
  }

  boardComponent() {
    return (
      <div>
        <div className="Board" style={{display: "block"}}>
          <div className="shipBoard" style={{display: "inline-block"}}>
            <ul>
              <span>Your Ships!</span>
              {this.props.shipBoard.map((rowItem, rowIndex) => {
                return (
                  <div key={this.state.rowKey + rowIndex}>{rowItem.map((colItem, colIndex) => {
                    return (
                      <span key={this.state.colKey + colIndex} onClick={() => this.handleShipClick(rowIndex, colIndex)}><Rectangle width={35} height={35} fill={{color: this.handleColor(colItem, 'shipBoard')}} stroke={{color:'black'}} strokeWidth={2} /></span>
                    )
                  })}
                </div>
                )
              })}
          </ul>
          </div>
          <div className="attackBoard" style={{display: "inline-block"}}>
            <ul>
              <span>Your Attacking Board!</span>
              {this.props.attackBoard.map((rowItem, rowIndex) => {
                return (
                  <div key={this.state.rowKey + rowIndex}>{rowItem.map((colItem, colIndex) => {
                    return (
                      <span key={this.state.colKey + colIndex} onClick={() => this.handleAttackClick(rowIndex, colIndex)}><Rectangle width={35} height={35} fill={{color: this.handleColor(colItem, 'attackShip')}} stroke={{color:'black'}} strokeWidth={2} /></span>
                    )
                  })}
                </div>
                )
              })}
          </ul>
          </div>
        </div>
        <div style={{display: "block"}}>
        <div style={{display: "inline-block", padding:20}}>
          <div>
          Your Ships
        </div>
        <Rectangle width={35} height={35} fill={{color: 'orange'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        <div style={{display: "inline-block", padding: 10}}>
          <div>
          Your Ships Kaput
        </div>
        <Rectangle width={35} height={35} fill={{color: 'red'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        <div style={{display: "inline-block", padding: 10}}>
          <div>
            Opponent Attacked but Missed!
        </div>
        <Rectangle width={35} height={35} fill={{color: 'grey'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        <div style={{display: "inline-block", padding:10}}>
          <div>
            Opponent Ships Attacked
        </div>
        <Rectangle width={35} height={35} fill={{color: 'green'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        <div style={{display: "inline-block", padding:10}}>
          <div>
            Opponent Ocean Attacked (no ship)
        </div>
        <Rectangle width={35} height={35} fill={{color: 'DarkBlue'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        <div style={{display: "inline-block", padding:10}}>
          <div>
            Haven't Attacked Yet!
        </div>
        <Rectangle width={35} height={35} fill={{color: 'SkyBlue'}} stroke={{color:'black'}} strokeWidth={3}/>
        </div>
        </div>
        <input type="text" value={this.state.text} onChange={(e) => this.handleTextChange(e)} placeholder="Play with your foe" onKeyPress={(e) => this.handleKeyPress(e)}></input>
        <button onClick={() => this.handleMessageSend()}>Send Message</button>
        {this.showMessages()}
      </div>
    )
  }

  gameWonOrLostComponent() {
    if (this.state.gameEndState) {
      if (this.state.win) {
        return (
          <iframe title="Queen" width="1300" height="750" src="https://www.youtube.com/embed/04854XqcfCY?autoplay=1&start=114" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
        )
      } else {
        return (
          <iframe title="GameOver" width="1300" height="750" src="https://www.youtube.com/embed/dsx2vdn7gpY?autoplay=1" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
        )
      }
    }
  }

  render() {
    return (
      <div>
        {this.state.gameEndState ? this.gameWonOrLostComponent(): this.boardComponent()}
    </div>
    );
  }
}

class Message extends Component {
  constructor(props) {
    super(props)
    this.state = {
    }
  }

  render() {
    return (
      <div style={{}}>
        {this.props.player === "ourPlayer" ? <div>You: {this.props.message} </div> : <div>Your Enemy: {this.props.message}</div>}
    </div>
    );
  }
}
export default App;
