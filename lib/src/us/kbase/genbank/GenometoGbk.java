package us.kbase.genbank;

import us.kbase.common.service.Tuple4;
import us.kbase.kbasegenomes.Feature;
import us.kbase.kbasegenomes.Genome;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/17/14
 * Time: 9:41 PM
 */
public class GenometoGbk {


    private static boolean debug = true;
    final static String molecule_type_short = "DNA";
    final static String molecule_type_long = "genome DNA";


    public static void writeGenbankFile(Genome genome, String contigId, 
            String contigName, String contigSequence, File outputFile) throws Exception {
        StringBuffer out = createHeader(genome, contigId, 1, contigSequence.length(), 
                contigName);
        out = createFeatures(genome, contigId, contigName, out);
        out.append("CONTIG      join(" + contigId + ":1.." + contigSequence.length() + ")\n");
        out.append("ORIGIN\n");
        PrintWriter pw = new PrintWriter(outputFile);
        pw.print(out);
        pw.flush();
        formatDNASequence(contigSequence, 10, 60, pw);
        pw.append("\n//\n");
        pw.close();
    }

    /**
     * @param contig_id
     * @param contigssize
     * @param contiglen
     * @param contig_name
     * @return
     */
    private static StringBuffer createHeader(Genome genome, String contig_id, int contigssize, long contiglen, String contig_name) {
        StringBuffer out = new StringBuffer("");
        out.append("LOCUS       " + contig_id + "             " + contigssize + " bp    " +
                "DNA     circular CON 10-JUN-2013\n");
        out.append("DEFINITION  " + genome.getScientificName() + " genome.\n");
        out.append("ACCESSION   " + contig_id + "\n");
        out.append("SOURCE      " + genome.getScientificName() + "\n");
        out.append("  ORGANISM  " + genome.getScientificName() + "\n");
        final String rawTaxonomy = genome.getTaxonomy();

        if (rawTaxonomy != null) {
            // format taxonomy string
            String[] alltax = rawTaxonomy.split(" ");

            StringBuffer formatTax = new StringBuffer("");

            int counter = 0;
            int index = 0;
            while (index < alltax.length) {
                formatTax.append(alltax[index]);
                if (index < alltax.length - 1)
                    formatTax.append(" ");
                counter += alltax[index].length() + 1;
                index++;

                // split formatted taxonomy across multiple lines if
                // at least 65 characters long
                if (counter >= 65 || rawTaxonomy.length() < 80) {
                    formatTax.append("\n");
                    formatTax.append("            ");
                    counter = 0;
                }
            }

            out.append("            " + formatTax + ".\n");
        }

        Long completeness = genome.getComplete();
        if (completeness == null)
            completeness = new Long(0);
        out.append(" COMMENT            COMPLETENESS: " + (completeness == 1 ? "full length" : "incomplete") + ".\n");

        out.append("                    Exported from the DOE KnowledgeBase.\n");


        out.append("FEATURES             Location/Qualifiers\n");
        out.append("     source          1.." + (contiglen == 0 ? 1 : contiglen) + "\n");
        out.append("                     /organism=\"" + genome.getScientificName() + "\"\n");
        out.append("                     /mol_type=\"" + molecule_type_long + "\"\n");
        //out += "                     /strain=\"\"\n";

        String taxId = null;
        Map<String, Object> addprops = genome.getAdditionalProperties();
        if (addprops != null) {
            for (String k : addprops.keySet()) {
                //System.out.println("addprops " + k + "\t" + addprops.get(k));
                if (k.equals("tax_id"))
                    taxId = "" + (Integer) addprops.get(k);

            }
        }

        if (taxId != null)
            out.append("                     /db_xref=\"taxon:" + taxId + "\"\n");

        return out;
    }

    /**
     * @param contig_id
     * @param contig_name
     * @param out
     * @return
     */
    private static StringBuffer createFeatures(Genome genome, String contig_id, String contig_name, StringBuffer out) {
        List<Feature> features = genome.getFeatures();

        System.out.println("all features " + features.size());
        for (int i = 0; i < features.size(); i++) {
            Feature cur = features.get(i);
            List<Tuple4<String, Long, String, Long>> location = cur.getLocation();

            //match features to their contig
            if ((contig_id == null && contig_name == null) || location.get(0).getE1().equals(contig_name) || location.get(0).getE1().equals(contig_id)) {
                String id = null;
                try {
                    final List<String> aliases = cur.getAliases();
                    if (aliases != null) {
                        try {
                            id = aliases.get(0);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                    if (id == null)
                        id = cur.getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String function = cur.getFunction();
                String[] allfunction = {""};
                if (function != null) {
                    function = function.replace('\"', ' ');

                    final int ind1 = function.indexOf(" /protein_id=");
                    if (ind1 != -1)
                        function = function.substring(0, ind1);
                    allfunction = function.split(" ");
                } else {
                    function = "";
                }


                StringBuffer formatNote = getAnnotation(function, allfunction, 51, 58, debug);
                StringBuffer formatFunction = getAnnotation(function, allfunction, 48, 58, debug);//51,58);

                if (id.indexOf(".opr.") == -1 && id.indexOf(".prm.") == -1 && id.indexOf(".trm.") == -1) {

                    if (cur.getType().equals("CDS")) //id.indexOf(".rna.") == -1)
                        out.append("     gene            ");
                    else {
                        if (function.indexOf("tRNA") != -1) {
                            out.append("     tRNA            ");
                        } else {
                            out.append("     misc_RNA        ");
                        }
                    }
                    out = getCDS(out, location);
                    out.append("                     /gene=\"" + id + "\"\n");
                    out.append("                     /locus_tag=\"" + id + "\"\n");
                    //out += "                     /db_xref=\"GeneID:2732620\"\n";
                    if (cur.getType().equals("CDS")) {
                        out.append("     CDS             ");
                        out = getCDS(out, location);
                        out.append("                     /gene=\"" + id + "\"\n");
                        out.append("                     /locus_tag=\"" + id + "\"\n");
                    }

                    out.append("                     /note=\"" + formatNote);
                    //out += "                     /codon_start=1\n";
                    //out += "                     /transl_table=11\n";
                    out.append("                     /product=\"" + id + "\"\n");
                    out.append("                     /function=\"" + new String(formatFunction));

                    if (cur.getType().equals("CDS")) {
                        out.append("                     /protein_id=\"" + id + "_prot\"\n");
                        out.append("                     /codon_start=1\n");
                        out.append("                     /transl_table=11\n");
                        /*if (formatNote.indexOf("two component transcriptional regulator") != -1) {
                            System.out.println("added :" + str + ":");
                            System.out.println("added :" + function + ":");
                        }*/
                    }

                    List<String> aliases = cur.getAliases();
                    if (aliases != null) {
                        for (String s : aliases) {
                            //System.out.println("adding alias " + s);
                            out.append("                     /db_xref=\"id:" + s + "\"\n");
                        }
                    }
                    //out += "                     /db_xref=\"GeneID:2732620\"\n";

                    //gene

                    final String proteinTranslation = cur.getProteinTranslation();
                    //System.out.println(proteinTranslation);
                    if (proteinTranslation != null)
                        out.append("                     /translation=\"" + formatString(proteinTranslation, 44, 58));
                    //else
                    //    System.out.println("op? " + id);
                }

                //if (test)
                //    System.exit(0);
                //}
            }
        }
        return out;
    }

    /**
     * @param function
     * @param allfunction
     * @return
     */
    private static StringBuffer getAnnotation(String function, String[] allfunction, int first, int next, boolean debug) {
        //if (debug)
        //    System.out.println("getAnnotation " + first + "\t" + next);
        StringBuffer formatFunction = new StringBuffer("");
        //73
        boolean isfirst = true;
        if (function.length() < first) {
            formatFunction.append(function + "\"\n");
        } else {
            int counter2 = 0;
            int index2 = 0;
            while (index2 < allfunction.length) {

                counter2 += allfunction[index2].length() + 1;

                if (((isfirst && counter2 >= first) || counter2 >= next)) {
                    // if (debug)
                    //    System.out.println("new line");
                    if (isfirst)
                        isfirst = false;

                    if (index2 < allfunction.length) {
                        formatFunction.append("\n");
                        formatFunction.append("                     ");
                        formatFunction.append(allfunction[index2]);
                        counter2 = allfunction[index2].length();
                        if (index2 < allfunction.length - 1) {
                            counter2++;
                            formatFunction.append(" ");
                        } else
                            formatFunction.append("\"\n");
                    }
                } else {
                    if (index2 < allfunction.length) {
                        formatFunction.append(allfunction[index2]);
                        if (index2 < allfunction.length - 1) {
                            counter2++;
                            formatFunction.append(" ");
                        } else
                            formatFunction.append("\"\n");
                    } else
                        formatFunction.append("\"\n");
                }

                index2++;
            }
        }
        if (formatFunction.length() == 0) {
            formatFunction.append("\"\n");
        }

        if (formatFunction.indexOf("\"\n") != formatFunction.length() - 2)
            formatFunction.append("\"\n");
        return formatFunction;
    }

    /**
     * @param out
     * @param location
     * @return
     */
    private static StringBuffer getCDS(StringBuffer out, List<Tuple4<String, Long, String, Long>> location) {
        int added = 0;
        boolean complement = false;
        boolean join = false;
        for (int n = 0; n < location.size(); n++) {
            Tuple4<String, Long, String, Long> now4 = location.get(n);
            //System.out.println("getCDS " + now4);
            if (added == 0 && now4.getE3().equals("-")) {
                out.append("complement(");
                complement = true;
            }
            //System.out.println("complement " + complement + "\t" + now4.getE3());
            if (location.size() > 1) {
                if (added == 0)
                    out.append("join(");
                join = true;
            }

            if (!complement) {
                //System.out.println("location +");
                out.append(now4.getE2() + ".." + (now4.getE2() + (long) now4.getE4() - 1));
            } else {
                //System.out.println("location -");
                out.append((now4.getE2() - (long) now4.getE4() + 1) + ".." + now4.getE2());
            }

            if (location.size() > 0 && n < location.size() - 1)
                out.append(",");
            added++;

            //complement = false;
        }
        if (complement && join)
            out.append("))\n");
        else if (complement || join) {
            out.append(")\n");
        } else
            out.append("\n");


        return out;
    }


    /**
     * @param s
     * @param one
     * @param two
     * @return
     */
    private static StringBuffer formatString(String s, int one, int two) {
        s = s.replace("\"", "");
        //StringBuilder out = new StringBuilder("");
        StringBuffer out = new StringBuffer("");
        boolean first = true;
        for (int start = 0; start < s.length(); ) {
            if (first) {
                int last = Math.min(s.length(), start + one);
                boolean isLast = false;
                if (last == s.length())
                    isLast = true;
                out.append(s.substring(start, last));
                if (isLast)
                    out.append("\"\n");
                else {
                    out.append("\n");
                }
                first = false;
                start += one;
            } else {
                int last = Math.min(s.length(), start + two);
                //System.out.println(s.length() + "\t" + (start + two));
                out.append("                     ");
                boolean isLast = false;
                if (last == s.length())
                    isLast = true;
                out.append(s.substring(start, last));
                start += two;
                if (isLast) {
                    //out.append(s.substring(start, s.length()-1));
                    out.append("\"\n");
                }
                //} else if (start < s.length()) {
                else
                    out.append("\n");
                //} //else if (start < s.length()) {
                //    out.append("\n");
                //} //else
                //  out.append("\n");
            }
        }

        return out;
    }

    /**
     * @param s
     * @param charnum
     * @param linenum
     * @return
     */
    private StringBuffer formatDNASequence(String s, int charnum, int linenum) {
        //StringBuilder out = new StringBuilder("");
        StringBuffer out = new StringBuffer("");

        //out += "        1 tctcgcagag ttcttttttg tattaacaaa cccaaaaccc atagaattta atgaacccaa\n";//10

        out.append("        1 ");
        int index = 1;
        int counter = 0;
        for (int last = 0; last < s.length(); ) {
            int end = Math.min(s.length(), last + charnum);
            //if (end > s.length())
            //   end = s.length();
            //System.out.println("DNA " + last + "\t" + end);
            out.append(s.substring(last, end));
            last += charnum;
            counter++;
            if (counter == 6 && s.length() > end) {
                out.append("\n");
                index += 60;
                String indexStr = "" + index;
                int len = indexStr.length();
                char[] ch = new char[9 - len];
                Arrays.fill(ch, ' ');
                String padStr = new String(ch);
                out.append(padStr + indexStr + " ");
                counter = 0;
            } else
                out.append(" ");
        }
        if (out.charAt(out.length() - 1) == (' '))
            out.deleteCharAt(out.length() - 1);
        out.append("\n");
        return out;
    }

    private static void formatDNASequence(String s, int charnum, int linenum, PrintWriter out) {
        out.append("        1 ");
        int index = 1;
        int counter = 0;
        for (int last = 0; last < s.length(); ) {
            int end = Math.min(s.length(), last + charnum);
            out.append(s.substring(last, end));
            last += charnum;
            counter++;
            if (counter == 6 && s.length() > end) {
                out.append("\n");
                index += 60;
                String indexStr = "" + index;
                int len = indexStr.length();
                char[] ch = new char[9 - len];
                Arrays.fill(ch, ' ');
                String padStr = new String(ch);
                out.append(padStr + indexStr);
                counter = 0;
                if (last + 1 < s.length())
                    out.append(" ");
            } else {
                if (last + 1 < s.length())
                    out.append(" ");
            }
        }
        out.append("\n");
    }

}
