package write;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.stream.*;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class XMLWriteFrame extends JFrame
{
    private RectangleComponent comp;
    private JFileChooser chooser;

    public XMLWriteFrame()
    {
        chooser = new JFileChooser();

        comp = new RectangleComponent();
        add(comp);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem newItem = new JMenuItem("New");
        menu.add(newItem);
        newItem.addActionListener(event -> comp.newDrawing());

        JMenuItem openItem = new JMenuItem("Open");
        menu.add(openItem);
        openItem.addActionListener(event -> openDocument());

        JMenuItem saveItem = new JMenuItem("Save using DOM/XSLT");
        menu.add(saveItem);
        saveItem.addActionListener(event -> saveDocument());

        JMenuItem saveStAXItem = new JMenuItem("Save using StAX");
        menu.add(saveStAXItem);
        saveStAXItem.addActionListener(event -> saveStAX());

        JMenuItem exitItem = new JMenuItem("Close");
        menu.add(exitItem);
        exitItem.addActionListener(event -> System.exit(0));

        pack();
    }

    public void openDocument()
    {
//        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
//            return;
//        File file = chooser.getSelectedFile();
//        XMLInputFactory factory = XMLInputFactory.newInstance();
//        try
//        {
//            XMLStreamReader reader = factory.createXMLStreamReader(Files.newInputStream(file.toPath()));
//            try
//            {
//                comp.readDocument(reader);
//            }
//            finally
//            {
//                reader.close();
//            }
//        }
//        catch (IOException | XMLStreamException ex)
//        {
//            ex.printStackTrace();
//        }
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        Path filename = chooser.getSelectedFile().toPath();
        try
        {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(filename));
            comp.readDocument(inputStream);
            comp.repaint();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveDocument()
    {
        try
        {
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;
            File file = chooser.getSelectedFile();
            Document doc = comp.buildDocument();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    "http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd");
            t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                    "-//W3C//DTD SVG 20000802//EN");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.transform(new DOMSource(doc),
                    new StreamResult(Files.newOutputStream(file.toPath())));
        }
        catch (TransformerException | IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void saveStAX()
    {
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        File file = chooser.getSelectedFile();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try
        {
            XMLStreamWriter writer =
                    factory.createXMLStreamWriter(Files.newOutputStream(file.toPath()));
            try
            {
                comp.writeDocument(writer);
            }
            finally
            {
                writer.close();
            }
        }
        catch (XMLStreamException | IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
