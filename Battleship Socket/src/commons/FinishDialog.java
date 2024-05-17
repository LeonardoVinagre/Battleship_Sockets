/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commons;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author leona
 */
public class FinishDialog extends JDialog {
    private int resultado = 0;

    public FinishDialog(JFrame parent, String titulo, String mensagem) {
        super(parent, titulo, true);
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel(mensagem);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JButton botao = new JButton("Clique aqui");
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultado = 1; // Defina o valor que deseja retornar
                dispose(); // Fecha o diálogo
            }
        });

        add(botao, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public int getResult() {
        return resultado;
    }
}
