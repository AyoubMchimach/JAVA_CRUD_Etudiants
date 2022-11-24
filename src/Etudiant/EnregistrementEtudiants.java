package Etudiant;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class EnregistrementEtudiants extends JFrame {

    //Variables ************************************

    ConnectionEtudiant con = new ConnectionEtudiant();
    String path = null;
    byte[] userimage = null;
    Statement pst;
    ResultSet rs;
    JLabel lblTitre,lblcode,lblnom,lblclasse,lblsexe,image1;
    JTextField txtcode,txtnom;
    JComboBox combosexe,comboclasse;
    JButton btnenregistrer,btnsupprimer,btnelecharger;
    JTable table,table1;
    JScrollPane scroll,scroll1;

    //Variables ************************************

    public void init(){
        table1 = new JTable();
        scroll1 = new JScrollPane();
        scroll1.setBounds(10,280,770,130);
        scroll1.setViewportView(table1);
    }

    public EnregistrementEtudiants(){

        super.setTitle("GESTION DES ETUDIANTS");
        super.setSize(800,450);
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel pn = new JPanel();
        pn.setLayout(null);
        add(pn);
        pn.setBackground(new Color(180,180,180));


        //Partie titre **********************************************

        lblTitre = new JLabel("Géstion des Etudiants");
        lblTitre.setBounds(270,10,800,30);
        lblTitre.setFont(new Font("Arial",Font.BOLD,24));
        lblTitre.setForeground(Color.black);
        pn.add(lblTitre);

        //Partie Eleve ************************************************

        //Numero label
        lblcode = new JLabel("Numéro d’étudiant :");
        lblcode.setBounds(40,60,800,30);
        lblcode.setFont(new Font("Arial",Font.BOLD,15));
        lblcode.setForeground(Color.black);
        pn.add(lblcode);

        //Numero textfield

        txtcode = new JTextField();
        txtcode.setBounds(200,60,150,30);
        txtcode.setFont(new Font("Arial",Font.BOLD,13));
        pn.add(txtcode);


        //Nom label

        lblnom = new JLabel("Nom et Prénom :");
        lblnom.setBounds(40,95,800,30);
        lblnom.setFont(new Font("Arial",Font.BOLD,15));
        lblnom.setForeground(Color.black);
        pn.add(lblnom);

        //Nom textfield

        txtnom = new JTextField();
        txtnom.setBounds(200,95,200,30);
        txtnom.setFont(new Font("Arial",Font.BOLD,13));
        pn.add(txtnom);

        //Sexe label
        lblsexe = new JLabel("Sexe :");
        lblsexe.setBounds(40,130,800,30);
        lblsexe.setFont(new Font("Arial",Font.BOLD,15));
        lblsexe.setForeground(Color.black);
        pn.add(lblsexe);

        //Sexe combobox
        combosexe = new JComboBox();
        combosexe.setBounds(200,130,200,30);
        combosexe.setFont(new Font("Arial",Font.PLAIN,13));
        combosexe.addItem("");
        combosexe.addItem("Masculin");
        combosexe.addItem("Feminin");
        pn.add(combosexe);

        //Classe label
        lblclasse = new JLabel("Classe :");
        lblclasse.setBounds(40,165,800,30);
        lblclasse.setFont(new Font("Arial",Font.BOLD,15));
        lblclasse.setForeground(Color.black);
        pn.add(lblclasse);

        //Classe combobox
        comboclasse = new JComboBox();
        comboclasse.setBounds(200,165,200,30);
        comboclasse.setFont(new Font("Arial",Font.PLAIN,13));
        comboclasse.addItem("");
        comboclasse.addItem("3EME GIIA");
        comboclasse.addItem("4EME IDIA");
        comboclasse.addItem("4EME GI");
        comboclasse.addItem("5EME GI");
        pn.add(comboclasse);

        //Photo

        image1 = new JLabel();
        image1.setBounds(530,60,150,150);
        image1.setFont(new Font("Arial",Font.BOLD,15));
        image1.setBackground(new java.awt.Color(255,0,0));
        image1.setFont(new java.awt.Font("Yu Gothic Light",0,18));
        image1.setHorizontalAlignment(SwingConstants.CENTER);
        image1.setBorder(BorderFactory.createEtchedBorder());
        image1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                image1MouseClicked(e);
            }
            private void image1MouseClicked(MouseEvent evt){
                JFileChooser pic = new JFileChooser();
                pic.showOpenDialog(null);

                File picture = pic.getSelectedFile();

                path = picture.getAbsolutePath();
                BufferedImage img;
                try {

                    img = ImageIO.read(pic.getSelectedFile());
                    ImageIcon imageic = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(150,150,Image.SCALE_DEFAULT));
                    image1.setIcon(imageic);
                    File image = new File(path);
                    FileInputStream fis = new FileInputStream(image);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];

                    for (int i; (i = fis.read(buff)) != -1;){
                        bos.write(buff,0,i);
                    }
                    userimage = bos.toByteArray();


                }catch(Exception exp){
                    exp.printStackTrace();
                }
            }
        });
        pn.add(image1);

        //Button d'Ajout **********************************************

        btnenregistrer = new JButton("Enregistrer");
        btnenregistrer.setBounds(200,230,150,30);
        btnenregistrer.setFont(new Font("Arial",Font.BOLD,15));
        btnenregistrer.setForeground(Color.black);
        btnenregistrer.setBackground(new java.awt.Color(173,216,230));

        btnenregistrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String num,nom,sexe,classe;

                num = txtcode.getText();
                nom = txtnom.getText();
                sexe = combosexe.getSelectedItem().toString();
                classe = comboclasse.getSelectedItem().toString();

                String rq = "insert into tb_eleve(code,nom,sexe,classe,photo) values(?,?,?,?,?)";
                try{
                    PreparedStatement ps = con.maConnetion().prepareStatement(rq);
                    ps.setString(1,num);
                    ps.setString(2,nom);
                    ps.setString(3,sexe);
                    ps.setString(4,classe);
                    ps.setBytes(5,userimage);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Etudiant Enregistré !!!",null,JOptionPane.INFORMATION_MESSAGE);

                    con.maConnetion().close();

                }catch(Exception exp){
                    JOptionPane.showMessageDialog(null,"ERREUR !!!"+ exp.getMessage(),null,JOptionPane.ERROR_MESSAGE);
                }
                dispose();
                EnregistrementEtudiants etd = new EnregistrementEtudiants();
                etd.setVisible(true);
            }
        });
        pn.add(btnenregistrer);

        //Button de suppression **********************************************
        btnsupprimer = new JButton("Supprimer");
        btnsupprimer.setBounds(370,230,150,30);
        btnsupprimer.setFont(new Font("Arial",Font.BOLD,15));
        btnsupprimer.setForeground(Color.black);
        btnsupprimer.setBackground(new java.awt.Color(173,216,230));

        btnsupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String num;

                num = txtcode.getText();

                String rq = "delete from tb_eleve where code ='"+num+"'";
                try{
                   pst = con.maConnetion().createStatement();
                   pst.executeUpdate(rq);
                   JOptionPane.showMessageDialog(null,"Eleve supprimé !!!",null,JOptionPane.INFORMATION_MESSAGE);

                }catch(Exception exp){
                    JOptionPane.showMessageDialog(null,"ERREUR !!!"+ exp.getMessage(),null,JOptionPane.ERROR_MESSAGE);
                }
                dispose();
                EnregistrementEtudiants etd = new EnregistrementEtudiants();
                etd.setVisible(true);
            }
        });
        pn.add(btnsupprimer);


        //Liste des étudiants ************************************************

        DefaultTableModel model = new DefaultTableModel();
        init();
        pn.add(scroll1);
        model.addColumn("Numero d'étudiant");
        model.addColumn("Nom et Prénom");
        model.addColumn("Sexe");
        model.addColumn("Classe");

        table1.setModel(model);
        String sql = "select * from tb_eleve order by code desc";
        try{
            pst = con.maConnetion().createStatement();
            rs = pst.executeQuery(sql);
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getString("sexe"),
                        rs.getString("classe"),
                });
            }

        }catch (Exception exp){
            JOptionPane.showMessageDialog(null,"ERREUR !!!"+ exp.getMessage(),null,JOptionPane.ERROR_MESSAGE);
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                table1MouseReleased(e);
            }
            public void table1MouseReleased(MouseEvent evt){
                int selectionner = table1.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                txtcode.setText(model.getValueAt(selectionner,0).toString());
                txtnom.setText(model.getValueAt(selectionner,1).toString());
                combosexe.setSelectedItem(model.getValueAt(selectionner,2).toString());
                comboclasse.setSelectedItem(model.getValueAt(selectionner,3).toString());


            }
        });


        //Button de Recherche **********************************************

        btnelecharger = new JButton("RECHERCHE");
        btnelecharger.setBounds(360,60,150,30);
        btnelecharger.setFont(new Font("Arial",Font.BOLD,13));
        btnelecharger.setForeground(Color.black);
        btnelecharger.setBackground(new java.awt.Color(173,216,230));

        btnelecharger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btntelechargerActionPerformed(e);
            }
            private void btntelechargerActionPerformed(ActionEvent evt){
                String num;
                num = txtcode.getText();

                try{

                    String rq = "select * from tb_eleve where code=?";
                    PreparedStatement ps = con.maConnetion().prepareStatement(rq);
                    ps.setString(1,num);
                    rs = ps.executeQuery();
                    if(rs.next()==false){
                        JOptionPane.showMessageDialog(null,"étudiant inexistant",null,JOptionPane.ERROR_MESSAGE);
                        txtcode.setText("");
                    }else{
                        txtnom.setText(rs.getString(2).trim());
                        combosexe.setSelectedItem(rs.getString(3).trim());
                        comboclasse.setSelectedItem(rs.getString(4).trim());
                        try{
                            Blob blob1 = rs.getBlob("photo");
                            byte[] imagebyte = blob1.getBytes(1, (int) blob1.length());
                            ImageIcon imag = new ImageIcon(new ImageIcon(imagebyte).getImage().getScaledInstance(150,150,Image.SCALE_DEFAULT));
                            image1.setIcon(imag);
                        }catch(Exception exp){
                            JOptionPane.showMessageDialog(null,"ERREUR !!!"+ exp.getMessage(),null,JOptionPane.ERROR_MESSAGE);

                        }

                    }

                }catch(Exception exp){
                    JOptionPane.showMessageDialog(null,"ERREUR !!!"+ exp.getMessage(),null,JOptionPane.ERROR_MESSAGE);
                }
            }
        });

            pn.add(btnelecharger);

    }

    public static void main(String[] args) {
        EnregistrementEtudiants en = new EnregistrementEtudiants();
        en.setVisible(true);
    }
}
