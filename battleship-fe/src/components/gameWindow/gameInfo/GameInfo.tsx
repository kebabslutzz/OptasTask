import { Container } from '@mui/material';
import WavesIcon from '@mui/icons-material/Waves';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import BlockIcon from '@mui/icons-material/Block';
import AnchorIcon from '@mui/icons-material/Anchor';

const GameInfo: React.FC = () => {
	return (
		<Container className='game-rules'>
			<h2>Game Rules</h2>
			<p>Welcome to Battleships! The aim of the game is to sink all of ships randomly placed by the computer. </p>
			<p>
				In total there are <b>10</b> ships: <b>1</b> ship of lengths <b>5</b> and <b>4</b>, <b>2</b> ships of length{' '}
				<b>3</b>, and <b>3</b> ships of lengths <b>2</b> and <b>1</b>. You have <b>25</b> shots to sink them all.{' '}
			</p>
			<p>
				Start the game by clicking <b>Start Game</b> button at the top of the page.
			</p>
			<p>
				Click on a {<WavesIcon className='wave lower-icon' />} square to fire a shot. A{' '}
				{<BlockIcon className='miss lower-icon' />} square indicates a miss, a{' '}
				<DirectionsBoatIcon className='ship lower-icon' /> square indicates a hit, and a{' '}
				<AnchorIcon className='destroyed lower-icon' /> indicates a sunken ship.
			</p>
			<p>
				You can restart the game at any time by clicking the <b>Restart Game</b> button at the top of the page. Good
				luck!
			</p>
		</Container>
	);
};

export default GameInfo;
