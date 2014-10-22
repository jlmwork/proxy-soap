package fr.erdf.nsge.sgel.proxy.soap.xml;

import org.junit.Test;

import prototypes.ws.proxy.soap.xml.XmlUtils;

public class XmlUtilsTest {

    @Test
    public void test() {
        String str = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v6=\"http://nsge.erdf.fr/sgel/aff/echange/service/v6_1\" xmlns:crtype=\"http://nsge.erdf.fr/SGEL/AFF/echange/types/v3\">\r\n"
                + "   <soapenv:Header/>\r\n"
                + "   <soapenv:Body>\r\n"
                + "      <v6:solderEtCloreAffairePrestationGDP>\r\n"
                + "         <contexte>\r\n"
                + "            <systemeId>?</systemeId>\r\n"
                + "         </contexte>\r\n"
                + "         <affaire>\r\n"
                + "            <affaireIdentifiant>00000050</affaireIdentifiant>\r\n"
                + "            <!--1 or more repetitions:-->\r\n"
                + "            <natureEvenements>CFN</natureEvenements>\r\n"
                + "            <prestation>\r\n"
                + "               <siContractuel>COSY</siContractuel>\r\n"
                + "               <prmIdentifiant>00000000000015</prmIdentifiant>\r\n"
                + "               <rang>2</rang>\r\n"
                + "               <realisation xsi:type=\"crtype:PrestationNonRealiseeType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "                  <motif>motifNREA</motif>\r\n"
                + "                  <commentaire>commentaireNREA</commentaire>\r\n"
                + "               </realisation>\r\n"
                + "               <facturation xsi:type=\"crtype:PrestationNonFactureeType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
                + "                  <motif>motifNFAC</motif>\r\n"
                + "                  <commentaire>commentaireNFAC</commentaire>\r\n"
                + "               </facturation>\r\n"
                + "               <!--Optional:-->\r\n"
                + "               <referencePoint>referencePoint2</referencePoint>\r\n"
                + "               <!--Optional:-->\r\n"
                + "               <typePoint>PADT</typePoint>\r\n"
                + "            </prestation>\r\n"
                + "         </affaire>\r\n"
                + "      </v6:solderEtCloreAffairePrestationGDP>\r\n"
                + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";
        // bypass first one
        XmlUtils.cleanXmlRequest(str);
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            XmlUtils.cleanXmlRequest(str);
            System.out
                    .println("Time : " + (System.currentTimeMillis() - start));
            str = new StringBuilder(str).toString();
        }
    }
}
