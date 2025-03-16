package net.fredrikmeyer.opengl;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

/**
 * Utility class for creating GIF animations from a sequence of BufferedImage frames.
 * Based on the GifSequenceWriter by Elliot Kroo (<a href="http://elliot.kroo.net/software/java/GifSequenceWriter/">...</a>).
 */
public class GifSequenceWriter {
    protected ImageWriter gifWriter;
    protected ImageWriteParam imageWriteParam;
    protected IIOMetadata imageMetaData;

    /**
     * Creates a new GifSequenceWriter.
     *
     * @param outputStream the ImageOutputStream to be written to
     * @param imageType one of the imageTypes specified in BufferedImage
     * @param timeBetweenFramesMS the time between frames in milliseconds
     * @param loopContinuously whether the gif should loop repeatedly
     * @throws IIOException if no gif ImageWriters are found
     */
    public GifSequenceWriter(
            ImageOutputStream outputStream,
            int imageType,
            int timeBetweenFramesMS,
            boolean loopContinuously) throws IOException {
        // Get the first available GIF ImageWriter
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Found");
        }
        gifWriter = iter.next();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(imageType);

        // Get the metadata
        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

        // Configure the metadata for looping
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by GifSequenceWriter");

        IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;
        byte[] loopBytes = new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)};
        child.setUserObject(loopBytes);
        appExtensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        // Initialize the writer
        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
    }

    /**
     * Writes the next frame to the GIF animation.
     *
     * @param img BufferedImage to be written as the next frame
     * @throws IOException if an error occurs during writing
     */
    public void writeToSequence(BufferedImage img) throws IOException {
        gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
    }

    /**
     * Closes the GifSequenceWriter, flushing and closing the underlying stream.
     *
     * @throws IOException if an error occurs during closing
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }

    /**
     * Gets a node from the tree, creating it if it doesn't exist.
     *
     * @param rootNode the root IIOMetadataNode
     * @param nodeName the name of the node to get
     * @return the node
     */
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }
}