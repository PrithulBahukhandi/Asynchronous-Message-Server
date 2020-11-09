// Name: Prithul Bahukhandi
// UTA ID: 1001730554

package chat_client;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams

public class client_frame extends javax.swing.JFrame 
{
    String username, address = "localhost";
    ArrayList<String> users = new ArrayList();      // this list is for storing online users
    ArrayList<String> allusers=new ArrayList();     // this list is for storing log of all users
    int port = 2222;
    Boolean isConnected = false;
    
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;     
    String str;
    DefaultListModel dm=new DefaultListModel();         // this will display a list on the client gui
    
    
    //--------------------------//

//  https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams    
    public void ListenThread() 
    {
         Thread IncomingReader = new Thread(new IncomingReader());      // start thread for all connecting users
         IncomingReader.start();
    }
    
    //--------------------------//
//  https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams    
    //this method adds the user in the users list  if the user is not present in the all user list
    public void userAdd(String data) 
    {
        String[] s=data.split(","); 
        for(String tok: s){
        if(!users.contains(tok))
        {
            users.add(tok);         //adding the user to array list of online users
        }

        }
    }
//   https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams   
    // this method is for adding all the users in the list and displaying it on the gui of client
    public void userListAdd(){
        String[] temp= new String[users.size()];    //modifying the size of list
         users.toArray(temp);
         dm.clear();
         
         for(String token: temp)
         {
             dm.addElement(token);      // adding the user to the list dm
         }
    }
    

    
    //--------------------------//
 // https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams   
    public void userRemove(String data) 
    {
         ta_chat.append(data + " is now offline.\n"); //
    }
    
    //--------------------------//
    
    public void writeUsers() 
    {
         String[] tempList = new String[(users.size())];
         users.toArray(tempList);
         for (String token:tempList) 
         {
             //users.append(token + "\n");
         }
    }
    
    //--------------------------//
//  https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams   
    public void sendDisconnect() 
    {
        String bye = (username + ": :Disconnect");
        try
        {
            ta_chat.append(username +": has disconnected. \n");
            writer.println(bye); 
            writer.flush();
            
        } catch (Exception e) 
        {
            ta_chat.append("Could not send Disconnect message.\n");
        }
    }

    //--------------------------//
 
 //  https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams
    public void Disconnect() 
    {
        try 
        {
//            ta_chat.append("Disconnected.\n");
            sock.close();
        } catch(Exception ex) {
            ta_chat.append("Failed to disconnect. \n");
        }
        
        tf_username.setEditable(true);
        b_connect.setEnabled(true);

    }
    
    public client_frame() 
    {
        initComponents();
    }
    
    //--------------------------//
    
    public class IncomingReader implements Runnable
    {
        @Override
        public void run() 
        {
//            System.out.println("debug1");
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", toAll="All", dischat = "Dischat",send="Send",offline="offline";

            try 
            {
                while ((stream = reader.readLine()) != null) // reading input stream of the client
                {   
                     data = stream.split(":");      // split the string and compare data[2], different operations will be performed based on data[2]
//                     System.out.println(data[0] +" " +data[1]);

                     if (data[2].equals(toAll)) 
                     {
//                        System.out.println("am i coming here");
                        String test=data[3].substring(1, data[3].length()-1);
                         System.out.println(" "+test);
                        userAdd(" "+test);
                        userListAdd();
                        ta_chat.append(data[0] + ": " + data[1] + "\n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     } 
                     // whenever a user disconnects , the message will be printed on all online users gui
                     else if (data[2].equals(dischat)){ 
                        ta_chat.append(data[0] + ": " + data[1] + "\n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // not using this in lab2
                     else if (data[2].equals(send)){
                         ta_chat.append(data[0] +" to " +data[3] +": " +data[1]+"\n");
                     }
                     // not using this in lab2 since messages should be sent to users even if they are offline
                     else if(data[2].equals(offline)){
                         ta_chat.append(data[3] +": is offline. Message not sent \n");
                     }
                     // remove the users from list of online users if they disconnect
                     else if (data[2].equals(disconnect)) 
                     {
                         userRemove(data[0]);
                     } 
                     // not using this in lab2
                     else if (data[2].equals("broadcasting")){
                         ta_chat.append(data[0] + " to All: " + data[1] + "\n");
                         ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // print the message on all client gui when server disconnects
                     else if (data[2].equals("shutdown")){
                         ta_chat.append("Server: has disconnected \n");
                         sock.close();
                         sendDisconnect();
                         Disconnect();
                         
                     }
                     // printing the data and time of messages received on client gui when they press check button along with the message
                     else if (data[2].equals("text")){
                        ta_chat.append("Received by server at: " +data[5]+":" +data[6]+":"+data[7]+"\n");   //timestamp
                        ta_chat.append(data[3]+": " +data[4]+"\n" );                                        // message
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // not using
                     else if (data[2].equals("pass")){
                         ta_chat.append(data[1] +"\n");
                         ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // printing only to online users, this is not used in lab2 since we have to send message to all users
                     else if (data[2].equals("sendToOnline")){
                         ta_chat.append(data[0] +": " +data[3] +": " +data[1] +"\n");
                         ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // if there are no messages in queue i.e. it is empty then this it will print queue is empty
                     else if (data[2].equals("emp")){
                        ta_chat.append("Queue is empty \n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     }
                     // printing message sent to queue using this method
                     else if (data[2].equals("msgToQueue")){
                        ta_chat.append("Message sent to queue: " +data[1] +"\n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());  
                     }
                     else if(data[2].equals("empty")){}
                }
           }catch(Exception ex) { System.out.println("Message not sent");}
        }
    }

    //--------------------------//
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        lb_address = new javax.swing.JLabel();
        tf_address = new javax.swing.JTextField();
        lb_username = new javax.swing.JLabel();
        tf_username = new javax.swing.JTextField();
        b_connect = new javax.swing.JButton();
        b_disconnect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        tf_chat = new javax.swing.JTextField();
        b_send = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        checkMsg = new javax.swing.JButton();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Client's frame");
        setName("client"); // NOI18N
        setResizable(false);

        lb_address.setText("Address : ");

        tf_address.setText("localhost");
        tf_address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_addressActionPerformed(evt);
            }
        });

        lb_username.setText("Username :");

        tf_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_usernameActionPerformed(evt);
            }
        });

        b_connect.setText("Connect");
        b_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_connectActionPerformed(evt);
            }
        });

        b_disconnect.setText("Disconnect");
        b_disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_disconnectActionPerformed(evt);
            }
        });

        ta_chat.setColumns(20);
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_send.setText("SEND");
        b_send.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_sendMouseClicked(evt);
            }
        });
        b_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_sendActionPerformed(evt);
            }
        });

        jLabel2.setText("Status");

        jList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListMouseClicked(evt);
            }
        });
        jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList);

        jLabel3.setText("All users list");

        checkMsg.setText("Check Messages");
        checkMsg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkMsgMouseClicked(evt);
            }
        });
        checkMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkMsgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lb_username, javax.swing.GroupLayout.PREFERRED_SIZE, 62, Short.MAX_VALUE)
                                    .addComponent(lb_address, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tf_username)
                                    .addComponent(tf_address, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)))
                            .addComponent(jLabel2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(b_connect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(b_disconnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tf_chat)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(checkMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 6, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_address)
                        .addComponent(tf_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(b_connect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_username)
                    .addComponent(b_disconnect)
                    .addComponent(lb_username))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(b_send, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tf_chat))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tf_addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_addressActionPerformed
       
    }//GEN-LAST:event_tf_addressActionPerformed

    private void tf_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_usernameActionPerformed
    
    }//GEN-LAST:event_tf_usernameActionPerformed

    private void b_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_connectActionPerformed
            // this method gets invoked whenever connect button is pressed'
            username = tf_username.getText();       //get the username from the field
            jList.setModel(dm);                     // setting the list model

            try 
            {
                //client request a connection from the server, server will check if the user is already online or not
                //if user is online from another client then it will give error and ask to enter different username
                //else the user will get connected
                sock = new Socket(address, port); // requesting connection  
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect"); // passing to client output stream
                writer.flush(); 
                String incoming= reader.readLine();
                if(incoming.equals("username is used.. try another")){ // error if user is already connected will be shown on client gui
                ta_chat.append(incoming +"\n");
                sock.close();
                }
                else{
//                    System.out.println(users.toString());
//                    userAdd(username);
//                    ta_chat.append(username +": has connected. \n");
                    tf_username.setEditable(false);
                    b_connect.setEnabled(false);
                }
                
            } 
            catch (Exception ex) 
            {
                ta_chat.append("Cannot Connect! Try Again. \n");
                tf_username.setEditable(true);
            }
            
            ListenThread();
            
        
    }//GEN-LAST:event_b_connectActionPerformed

    private void b_disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_disconnectActionPerformed
        sendDisconnect();
        Disconnect();
    }//GEN-LAST:event_b_disconnectActionPerformed

    private void b_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_sendActionPerformed

    }//GEN-LAST:event_b_sendActionPerformed

    private void b_sendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_sendMouseClicked
        // TODO add your handling code here:
        // whenever send button is clicked this method gets invoked
        //if the send button is pressed and no selection is made from the list 
        //it will send the message to all the users (broadcast)
        if(jList.isSelectionEmpty()){
            if((tf_chat.getText()).equals("")){     //if message is empty odnt send anything
                tf_chat.setText("");
                tf_chat.requestFocus();
            }
            else{               // if message not empty then broadcast the message
                try{
                    writer.println(username + ":" + tf_chat.getText() + ":" +"Broadcast");  //writing on client output stream
                    writer.flush();
                }catch(Exception ex){ta_chat.append("Message was not sent. \n");}
            }
        }
        // if selection is made from list then the message will be sent to only those users
        // the message will not be broadcasted
        else{
        String listt=jList.getSelectedValuesList().toString();  // getting list of selected users from the list
        listt= listt.substring(1, listt.length()-1);            // converting list to a suitable format
        listt=listt.replaceAll(",", ";");
        listt=listt.replaceAll(" ","");
        String nothing = "";
        if ((tf_chat.getText()).equals(nothing)) {      // if the message is empty dont send anything
            tf_chat.setText("");
            tf_chat.requestFocus();
        } else {
            try {                                       // send the message to selected users
               writer.println(username + ":" + tf_chat.getText() + ":" + "Send" +":" +listt);   // writing on client output stream
               writer.flush(); // flushes the buffer
            } catch (Exception ex) {
                ta_chat.append("Message was not sent. \n");
            }
            tf_chat.setText("");
            tf_chat.requestFocus();
        }

        tf_chat.setText("");
        tf_chat.requestFocus();
//        System.out.println(str);
        }
    }//GEN-LAST:event_b_sendMouseClicked

    private void jListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jListMouseClicked

    private void jListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jListValueChanged

    private void checkMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkMsgActionPerformed
        // TODO add your handling code here:
        // this method will be invoked if check button is pressed
        // this will look for all the messages in the user queue and display those message on client gui
        System.out.println("check button pressed");
        writer.println(username +":" +":" +"check");
        writer.flush();
    }//GEN-LAST:event_checkMsgActionPerformed

    private void checkMsgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkMsgMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_checkMsgMouseClicked

    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                new client_frame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_connect;
    private javax.swing.JButton b_disconnect;
    private javax.swing.JButton b_send;
    private javax.swing.JButton checkMsg;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lb_address;
    private javax.swing.JLabel lb_username;
    private javax.swing.JTextArea ta_chat;
    private javax.swing.JTextField tf_address;
    private javax.swing.JTextField tf_chat;
    private javax.swing.JTextField tf_username;
    // End of variables declaration//GEN-END:variables
}
