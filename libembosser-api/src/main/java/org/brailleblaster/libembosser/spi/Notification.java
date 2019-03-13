package org.brailleblaster.libembosser.spi;

import java.util.Locale;

/**
 * Provide notification information.
 * 
 * Notifications are used to provide information. They may be used where an exception would not be appropriate, such as being returned in a list from a method so that multiple notifications can be given at once.
 * 
 * @author Michael Whapples
 *
 */
public interface Notification {
	/**
	 * The types of notification.
	 * 
	 * @author Michael Whapples
	 *
	 */
	public static enum NotificationType {
		/**
		 * An error notification.
		 * 
		 * An error means that something is seriously wrong and it is unlikely to be possible to correct the situation. If an error notification is detected then the recommended action would be to stop the task.
		 */
		ERROR, 
		/**
		 * A warning notification.
		 * 
		 * A warning indicates that something is wrong but the process may still be able to complete even if there are inperfections. It may be possible to take action to correct a warning.
		 */
		WARNING,
		/**
		 * Informational notification.
		 * 
		 * An information notification simply provides some information about the process. It does not necessarily indicate that something is wrong. It is safe to assume that all is working as expected and to continue with any task.  
		 */
		INFO
	}
	/**
	 * Get the type of the notification.
	 * 
	 * @return The notification type.
	 */
	public NotificationType getNotificationType();
	/**
	 * Get the message in the system default locale.
	 * 
	 * The message is information which may be useful to the user.
	 * 
	 * @return The notification message.
	 */
	public String getMessage();
	/**
	 * Get the message in the specified locale.
	 * 
	 * @param locale The locale to be used for the message.
	 * @return The localised message.
	 */
	public String getMessage(Locale locale);
}
