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
	  DISC1_CONNECTED,
	  DISC1_DISCONNECTED,
	  DISC2_CONNECTED,
	  DISC2_DISCONNECTED,
	  DISC3_CONNECTED,
	  DISC3_DISCONNECTED,
	  EMP_ACTIVE,
	  EMP_PASSIVE,
	  EMP_PRESSED,
	  OPEN_CAHIR_LOCK,
	  CLOSE_CAHIR_LOCK,
	  INTERCOM_BUTTON_PRESSED,
	  RED_LAMP_ON,
	  RED_LAMP_OFF,
}
