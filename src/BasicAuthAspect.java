import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by OdedA on 04-May-16.
 */
@Aspect
public class BasicAuthAspect {


    @Pointcut("execution(@BasicAuth * *(..))")
    public void basicAuthAnnot() {}

//    @Pointcut("execution(public * *(..))")
//    public void publicMethod() {}

//    @Pointcut("basicAuthAnnot()")
//    public void publicMethodInsideAClassMarkedWithBasicAuth() {}

//    @Before("publicMethodInsideAClassMarkedWithBasicAuth()")
//    public void beforeMonitored(JoinPoint joinPoint) {
//        System.out.println("Clicked basicAuthAnnot function.");
//    }

    boolean isValid = false;
    @Around("basicAuthAnnot()")
    public void aroundBasicAuthAnnot(ProceedingJoinPoint point) {
//        boolean isValid = false;
        System.out.println(point.toString());



//1. Create the frame.
        JFrame frame = new JFrame("FrameDemo");

//2. Optional: What happens when the frame closes?
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//3. Create components and put them in the frame.
        JTextField passJText = new JTextField();
        passJText.setPreferredSize(new Dimension(160, 20));
        passJText.setEnabled(true);
        passJText.setHorizontalAlignment(4);

        JButton sendButton = new JButton("send");
        sendButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (passJText.getText().equals("admin")) {
//                if (true) {
                    try {
                        frame.setVisible(false);
                        frame.dispose();
                        point.proceed();
                    } catch (Throwable t) {
                        System.out.println("caught throwable, refer to BasicAuthAspect");
                    }
                } else {
                    System.out.println("\"" + passJText.getText() + "\"" +  " is wrong input");
                }
            }
        });

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Pass:"));
        myPanel.add(passJText);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel());
        myPanel.add(sendButton);
//
//        int result = JOptionPane.showConfirmDialog(null, myPanel,
//                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            System.out.println("x value: " + xField.getText());
//            System.out.println("y value: " + yField.getText());
//        }
        frame.add(myPanel);




//4. Size the frame.
//        frame.add(passJText);
//        frame.add(sendButton);
        frame.pack();

//5. Show it.
        frame.setVisible(true);










//        JPanel motherPanel = new JPanel();
//        motherPanel.setLayout(new BoxLayout(motherPanel, BoxLayout.Y_AXIS));
//
//        JPanel textPanel = new JPanel();
//        textPanel.setPreferredSize(new Dimension(160, 20));
//        textPanel.add(resultJText);
//
//
//        motherPanel.add(textPanel);
//        motherPanel.add(numberButtonsPanel);
//        motherPanel.add(functionButtonPanel);
//        add(motherPanel);
//
//        setTitle("ButtonTest");
//        setSize(180, 290);
//        setLocationByPlatform(true);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setVisible(true);








//        System.out.println("please enter 'y' to continue");
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String s = br.readLine();
//            if (s.equals("y")) {
//                try {
//                    point.proceed();
//                } catch (Throwable t) {
//                    System.out.println("caught throwable, refer to BasicAuthAspect");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("error");
//        }
    }
}
