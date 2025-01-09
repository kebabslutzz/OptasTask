import React from 'react';
import Grid from './Grid';
import './GameBoard.css';
import { Container } from '@mui/material';
import WavesIcon from '@mui/icons-material/Waves';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import BlockIcon from '@mui/icons-material/Block';
import AnchorIcon from '@mui/icons-material/Anchor';

const GameBoard = () => {
	return (
		<>
			<Container>
				<Grid />
			</Container>
			<Container className='game-rules'>
				<h2>Game Rules</h2>
				<p>Welcome to Battleships! The aim of the game is to sink all of ships randomly placed by the computer. </p>
				<p>
					In total there are 10 ships: 1 ship of length 5, 1 ships of length 4, 2 ships of length 3, and 3 ships of
					length 2 and 1. You have 25 shots to do so.{' '}
				</p>
				<p>Start the game by clicking start button at the top of the page</p>
				<p>
					Click on a {<WavesIcon className='wave lower-icon' />} square to fire a shot. A{' '}
					{<BlockIcon className='miss lower-icon' />} square indicates a miss, a{' '}
					<DirectionsBoatIcon className='ship lower-icon' /> square indicates a hit, and a{' '}
					<AnchorIcon className='destroyed lower-icon' /> indicates a sunken ship.
				</p>
				<p>You can restart the game at any time by clicking the restart button at the top of the page. Good luck!</p>
			</Container>
		</>
	);
};

export default GameBoard;
