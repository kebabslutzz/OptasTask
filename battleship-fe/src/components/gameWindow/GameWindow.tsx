import React from 'react';
import './GameWindow.css';
import { Container } from '@mui/material';

import Game from '../game/Game';
import GameInfo from './gameInfo/GameInfo';

const GameWindow = () => {
	return (
		<Container>
			<Game />
			<GameInfo />
		</Container>
	);
};

export default GameWindow;
