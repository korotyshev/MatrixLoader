package ua.vzaperti.matrix.util;

/**
 * Events for the MatrixLoader application. 
 * GUI will listen for incoming actions and react somehow on these events. 
 *
 */
public enum MatrixLoaderEvents {
	NOP, // No operation
	RESET, // Game reset command - set everything into initial state
	NEO_CONNECTED, // Neo wire connected
	NEO_DISCONNECTED,
	DISK1_CONNECTED, // Disk 1 inserted into the drive
	DISK1_DISCONNECTED,
	DISK2_CONNECTED,
	DISK2_DISCONNECTED,
	DISK3_CONNECTED,
	DISK3_DISCONNECTED,
}
