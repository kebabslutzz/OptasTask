import React from 'react';
import './renderGrid.css';
import WavesIcon from '@mui/icons-material/Waves';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import BlockIcon from '@mui/icons-material/Block';
import AnchorIcon from '@mui/icons-material/Anchor';

const renderGrid = (
	gridState: (boolean | null | 'destroyed')[][],
	handleClick: (row: number, col: number) => void,
	gameStarted: boolean,
	gameOver: boolean
) => {
	const grid = [];
	for (let row = 0; row < 10; row++) {
		for (let col = 0; col < 10; col++) {
			const cellState = gridState[row][col];
			grid.push(
				<button
					key={`${row}-${col}`}
					className={`grid-button ${cellState === null ? '' : cellState ? 'hit' : 'miss'}${
						!gameStarted === true ? ' not-started' : ''
					}`}
					onClick={() => handleClick(row, col)}
					disabled={!gameStarted || gameOver || cellState !== null}
				>
					{cellState === null ? (
						<WavesIcon className='wave' />
					) : cellState === true ? (
						<DirectionsBoatIcon className='ship' />
					) : cellState === 'destroyed' ? (
						<AnchorIcon className='destroyed' />
					) : (
						<BlockIcon className='miss' />
					)}
				</button>
			);
		}
	}
	return grid;
};

export default renderGrid;
