package co.uk.fluxanoia.util;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

// The TextQuery class, manages a window that takes text input
public class TextQuery {
	
	// The window
	private JFrame window;
	// The text field
	private TextField textField;
	// The enter button
	private JButton done, cancel;
	// Whether the button has been pressed or not
	private boolean pressed, cancelled;
	
	// Constructs the text query
	public TextQuery(String title) {
		this(title, new Point(250, 300));
	}
	public TextQuery(String title, Point p) {
		// Initialise values
		this.pressed = false;
		this.cancelled = false;
		this.window = new JFrame(title);
		this.window.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		this.textField = new TextField(40);
		this.done = new JButton("Done");
		this.cancel = new JButton("Cancel");
		// Add the ActionListener
		this.done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressed = true;
			}
		});
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled = true;
			}
		});
		// Add components
		this.window.add(new JLabel(title));
		this.window.add(textField);
		this.window.add(done);
		this.window.add(cancel);
		// Set the window position
		this.window.setLocation(p);
		// Pack the window
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.setUndecorated(true);
		this.window.pack();
	}
	
	// Displays the window
	public void show() {
		this.window.setVisible(true);
	}
	
	// Returns the text field data
	public String getText() {
		return textField.getText();
	}
	
	// Clears the text field
	public void clear() {
		this.textField.setText("");
	}
	
	// Hides the window
	public void hide() {
		this.window.setVisible(false);
	}
	
	// Returns whether the button has been pressed or not
	// and resets the value
	public boolean dropPressed() {
		if (pressed) {
			pressed = false;
			return true;
		}
		return false;
	}
	
	// Returns whether the button has been pressed or not
	// and resets the value
	public boolean dropCancelled() {
		if (cancelled) {
			cancelled = false;
			return true;
		}
		return false;
	}

}
