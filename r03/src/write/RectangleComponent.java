package write;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Random;

public class RectangleComponent extends JComponent
{
    private static final Dimension PREFERRED_SIZE = new Dimension(300, 200);

    private java.util.List<Rectangle2D> rects;
    private java.util.List<Color> colors;
    private Random generator;
    private DocumentBuilder builder;

    public RectangleComponent()
    {
        rects = new ArrayList<>();
        colors = new ArrayList<>();
        generator = new Random();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(false);
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public void newDrawing()
    {
        int n = 10 + generator.nextInt(20);
        rects.clear();;
        colors.clear();
        for (int i = 1; i <= n; ++i)
        {
            int x = generator.nextInt(getWidth());
            int y = generator.nextInt(getHeight());
            int width = generator.nextInt(getWidth() - x);
            int height = generator.nextInt(getHeight() - y);
            rects.add(new Rectangle2D.Double(x, y, width, height));
            int r = generator.nextInt(256);
            int g = generator.nextInt(256);
            int b = generator.nextInt(256);
            colors.add(new Color(r, g, b));
        }
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        if (rects.size() == 0) newDrawing();
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < rects.size(); ++i)
        {
            g2.setPaint(colors.get(i));
            g2.fill(rects.get(i));
        }
    }

    public Document buildDocument()
    {
        String namespace = "http://www.w3.org/2000/svg";
        Document doc = builder.newDocument();
        Element svgElement = doc.createElementNS(namespace, "svg");
        doc.appendChild(svgElement);
        svgElement.setAttribute("width", "" + getWidth());
        svgElement.setAttribute("height","" + getHeight());
        for (int i = 0; i < rects.size(); ++i)
        {
            Color c = colors.get(i);
            Rectangle2D r = rects.get(i);
            Element rectElement = doc.createElementNS(namespace, "rect");
            rectElement.setAttribute("x", "" + r.getX());
            rectElement.setAttribute("y", "" + r.getY());
            rectElement.setAttribute("width", "" + r.getWidth());
            rectElement.setAttribute("height", "" + r.getHeight());
            rectElement.setAttribute("fill", String.format("#%06x", c.getRGB() & 0xFFFFFF));
            svgElement.appendChild(rectElement);
        }
        return doc;
    }

    public void readDocument(InputStream inputStream)
    {
        String namespace = "http://www/w3.org/2000/svg";
        Document doc = builder.newDocument();
        try
        {
            long end;
            long start = System.currentTimeMillis();
            System.out.println("parsing...");
            doc = builder.parse(inputStream);
            end = System.currentTimeMillis();

            System.out.println(" parsed in " + (end - start) / 1000.0 + " seconds");
            Element svgElement = doc.getDocumentElement();
            NodeList rectNodes = svgElement.getChildNodes();

            rects.clear();
            colors.clear();
            for (int i = 0; i < rectNodes.getLength(); ++i)
            {
                Element rectElem = (Element) rectNodes.item(i);
                double x = Double.parseDouble(rectElem.getAttribute("x"));
                double y = Double.parseDouble(rectElem.getAttribute("y"));
                double width = Double.parseDouble(rectElem.getAttribute("width"));
                double height = Double.parseDouble(rectElem.getAttribute("height"));
                int r = Integer.parseInt(rectElem.getAttribute("fill").substring(1, 3), 16);
                int g = Integer.parseInt(rectElem.getAttribute("fill").substring(3, 5), 16);
                int b = Integer.parseInt(rectElem.getAttribute("fill").substring(5, 7), 16);
                rects.add(new Rectangle2D.Double(x, y, width, height));
                colors.add(new Color(r, g, b));
            }
        }
        catch (SAXException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeDocument(XMLStreamWriter writer) throws XMLStreamException
    {
        writer.writeStartDocument();
        writer.writeDTD("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20000802//EN\""
           + "\"http://www/w3/org/TR/20000/CR-SVG-20000802/DTD/svg-20000802.dtd\">");
        writer.writeStartElement("svg");
        writer.writeDefaultNamespace("http://www.w3.org/2000/svg");
        writer.writeAttribute("width", "" + getWidth());
        writer.writeAttribute("height", "" + getHeight());
        for (int i = 0; i < rects.size(); ++i)
        {
            Color c = colors.get(i);
            Rectangle2D r = rects.get(i);
            writer.writeEmptyElement("rect");
            writer.writeAttribute("x", "" + r.getX());
            writer.writeAttribute("y", "" + r.getY());
            writer.writeAttribute("width", "" + r.getWidth());
            writer.writeAttribute("height", "" + r.getHeight());
            writer.writeAttribute("fill", String.format("#%06x", c.getRGB() & 0xFFFFFF));
        }
        writer.writeEndDocument();
    }

    public Dimension getPreferredSize() { return PREFERRED_SIZE; }
}
