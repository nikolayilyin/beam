package beam.agentsim.events.handling;

import beam.sim.BeamServices;
import beam.utils.IntegerValueHashMap;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.utils.io.UncheckedIOException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import beam.agentsim.events.LoggerLevels;

/**
 * BEAM
 */
public class BeamEventsWriterXML extends BeamEventsWriterBase{
    public BeamEventsWriterXML(String outfilename, BeamEventsLogger beamEventLogger, BeamServices beamServices, Class<?> eventTypeToLog) {
        super(outfilename, beamEventLogger, beamServices, eventTypeToLog);


        writeHeaders();
    }



    @Override
    public void writeHeaders(){
        try {
            this.out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<events version=\"1.0\">\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void closeFile() {
        try {
            this.out.write("</events>");
            // I added a "\n" to make it look nicer on the console.  Can't say if this may have unintended side
            // effects anywhere else.  kai, oct'12
            // fails signalsystems test (and presumably other tests in contrib/playground) since they compare
            // checksums of event files.  Removed that change again.  kai, oct'12
            this.out.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Deprecated
    public void init(final String outfilename) {
        throw new RuntimeException("Please create a new instance.");
    }

    @Override
    public void reset(final int iter) {
    }

    //Modify the method to control logger and based on logging level writing specific attribute
    @Override
    protected void writeEvent(final Event event) {
        String LoggerLevel=this.beamServices.beamConfig().beam().outputs().defaultLoggingLevel();

        if(LoggerLevel.equals(LoggerLevels.OFF.toString())||LoggerLevel.equals("")){System.out.println("No Logs!");}
        else {
            try {
            this.out.append("\t<event ");
            Map<String, String> attr = event.getAttributes();

            for (Map.Entry<String, String> entry : attr.entrySet()) {

                if(LoggerLevel.equals(LoggerLevels.VERBOSE.toString())){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");

                }

                if(LoggerLevel.equals(LoggerLevels.REGULAR.toString())){
                    if (entry.getKey().equals("time")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }

                    if (entry.getKey().equals("type")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }

                    if (entry.getKey().equals("vehicle")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }

                    if (entry.getKey().equals("departure_time")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }

                    if (entry.getKey().equals("mode")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }


                }

                if(LoggerLevel.equals(LoggerLevels.SHORT.toString())){
                    if (entry.getKey().equals("time")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }

                    if (entry.getKey().equals("type")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                }
                    if (entry.getKey().equals("person")){
                        this.out.append(entry.getKey());
                        this.out.append("=\"");
                        this.out.append(encodeAttributeValue(entry.getValue()));
                        this.out.append("\" ");
                    }
                }


            }

            this.out.append(" />\n");
//			this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }}
    }

    // the following method was taken from MatsimXmlWriter in order to correctly encode attributes, but
    // to forego the overhead of using the full MatsimXmlWriter.
    /**
     * Encodes the given string in such a way that it no longer contains
     * characters that have a special meaning in xml.
     *
     * @see <a href="http://www.w3.org/International/questions/qa-escapes#use">http://www.w3.org/International/questions/qa-escapes#use</a>
     * @param attributeValue
     * @return String with some characters replaced by their xml-encoding.
     */
    private String encodeAttributeValue(final String attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        int len = attributeValue.length();
        boolean encode = false;
        for (int pos = 0; pos < len; pos++) {
            char ch = attributeValue.charAt(pos);
            if (ch == '<') {
                encode = true;
                break;
            } else if (ch == '>') {
                encode = true;
                break;
            } else if (ch == '\"') {
                encode = true;
                break;
            } else if (ch == '&') {
                encode = true;
                break;
            }
        }
        if (encode) {
            StringBuffer bf = new StringBuffer();
            for (int pos = 0; pos < len; pos++) {
                char ch = attributeValue.charAt(pos);
                if (ch == '<') {
                    bf.append("&lt;");
                } else if (ch == '>') {
                    bf.append("&gt;");
                } else if (ch == '\"') {
                    bf.append("&quot;");
                } else if (ch == '&') {
                    bf.append("&amp;");
                } else {
                    bf.append(ch);
                }
            }

            return bf.toString();
        }
        return attributeValue;

    }
}
