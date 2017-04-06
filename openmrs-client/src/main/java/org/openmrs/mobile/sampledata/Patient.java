package org.openmrs.mobile.sampledata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Patient implements Serializable {

    public String given_name;
    public String middle_name;
    public String family_name;
    public int age;
    public String id;
    public char gender;
    public int active_visit;
    private JSONArray patients = new JSONArray();

    private String patientsString = "[\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Chelsea\",\n" +
            "\t\t\"middle_name\": \"Sybill\",\n" +
            "\t\t\"family_name\": \"Brenna\",\n" +
            "\t\t\"age\": 89,\n" +
            "\t\t\"id\": \"A37BDFC5-52D3-15F7-AD2D-5BA1BC1D11F9\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Malcolm\",\n" +
            "\t\t\"middle_name\": \"Vanna\",\n" +
            "\t\t\"family_name\": \"Iola\",\n" +
            "\t\t\"age\": 37,\n" +
            "\t\t\"id\": \"9EE67E04-05EA-3C75-90C2-9C412F12F0B5\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Adrienne\",\n" +
            "\t\t\"middle_name\": \"Keaton\",\n" +
            "\t\t\"family_name\": \"Justine\",\n" +
            "\t\t\"age\": 65,\n" +
            "\t\t\"id\": \"F6F19F1C-ACF4-70BB-2D9A-79DC01F8484B\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Kelly\",\n" +
            "\t\t\"middle_name\": \"Melanie\",\n" +
            "\t\t\"family_name\": \"Gage\",\n" +
            "\t\t\"age\": 85,\n" +
            "\t\t\"id\": \"B9BE69CC-D323-4553-FAF1-E8E60A1F8A92\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Hoyt\",\n" +
            "\t\t\"middle_name\": \"Graiden\",\n" +
            "\t\t\"family_name\": \"Rina\",\n" +
            "\t\t\"age\": 35,\n" +
            "\t\t\"id\": \"52588CEA-39E1-76BE-5A47-8E615D376D62\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Julian\",\n" +
            "\t\t\"middle_name\": \"Marshall\",\n" +
            "\t\t\"family_name\": \"Macaulay\",\n" +
            "\t\t\"age\": 7,\n" +
            "\t\t\"id\": \"FB2515CC-F643-B142-23CF-6AC2FDE077B7\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Signe\",\n" +
            "\t\t\"middle_name\": \"Cathleen\",\n" +
            "\t\t\"family_name\": \"Nicholas\",\n" +
            "\t\t\"age\": 51,\n" +
            "\t\t\"id\": \"4BBE11D6-F4B2-E52D-E92B-4EEA3ABAF517\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Ignatius\",\n" +
            "\t\t\"middle_name\": \"Patrick\",\n" +
            "\t\t\"family_name\": \"Raymond\",\n" +
            "\t\t\"age\": 31,\n" +
            "\t\t\"id\": \"3F83D504-28BA-315B-90E6-C0575DE8761F\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Jayme\",\n" +
            "\t\t\"middle_name\": \"Hayes\",\n" +
            "\t\t\"family_name\": \"Patience\",\n" +
            "\t\t\"age\": 24,\n" +
            "\t\t\"id\": \"AE8610A1-E983-7868-309B-9B7ADA6AAC54\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Scarlet\",\n" +
            "\t\t\"middle_name\": \"Germane\",\n" +
            "\t\t\"family_name\": \"Zena\",\n" +
            "\t\t\"age\": 20,\n" +
            "\t\t\"id\": \"0D185D78-A53A-087B-E3C3-9DD70BA608A2\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Norman\",\n" +
            "\t\t\"middle_name\": \"Dale\",\n" +
            "\t\t\"family_name\": \"Norman\",\n" +
            "\t\t\"age\": 42,\n" +
            "\t\t\"id\": \"8CB1653B-D2FC-B6EB-B466-94DBD613CEB0\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Gavin\",\n" +
            "\t\t\"middle_name\": \"Kim\",\n" +
            "\t\t\"family_name\": \"Baker\",\n" +
            "\t\t\"age\": 97,\n" +
            "\t\t\"id\": \"BC9CCA0D-0BE8-FDDA-3038-EE48211F790B\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Colette\",\n" +
            "\t\t\"middle_name\": \"Candace\",\n" +
            "\t\t\"family_name\": \"Jemima\",\n" +
            "\t\t\"age\": 12,\n" +
            "\t\t\"id\": \"219BF334-2BD7-DDF8-E075-BEE99D5A845B\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Lev\",\n" +
            "\t\t\"middle_name\": \"Lars\",\n" +
            "\t\t\"family_name\": \"Keiko\",\n" +
            "\t\t\"age\": 88,\n" +
            "\t\t\"id\": \"B7FAC35B-B529-2055-3143-F32CA7966FAC\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Raya\",\n" +
            "\t\t\"middle_name\": \"Evelyn\",\n" +
            "\t\t\"family_name\": \"Hasad\",\n" +
            "\t\t\"age\": 30,\n" +
            "\t\t\"id\": \"D31BA966-8165-2917-71B1-C7A08EEE104F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Chloe\",\n" +
            "\t\t\"middle_name\": \"Eve\",\n" +
            "\t\t\"family_name\": \"Wyatt\",\n" +
            "\t\t\"age\": 93,\n" +
            "\t\t\"id\": \"DF3E67F5-34D9-0A90-84DE-938D4AED1B6C\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Kirsten\",\n" +
            "\t\t\"middle_name\": \"Kirsten\",\n" +
            "\t\t\"family_name\": \"Ramona\",\n" +
            "\t\t\"age\": 84,\n" +
            "\t\t\"id\": \"C9CD99F7-25F5-2370-71E0-A7FA02D77A5F\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Lareina\",\n" +
            "\t\t\"middle_name\": \"Venus\",\n" +
            "\t\t\"family_name\": \"Porter\",\n" +
            "\t\t\"age\": 18,\n" +
            "\t\t\"id\": \"A8E545AC-49A2-03AA-953D-212C436240A3\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Thomas\",\n" +
            "\t\t\"middle_name\": \"Mechelle\",\n" +
            "\t\t\"family_name\": \"Rina\",\n" +
            "\t\t\"age\": 30,\n" +
            "\t\t\"id\": \"B6B30694-2E14-326B-16E1-879D69A7E1EB\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Brittany\",\n" +
            "\t\t\"middle_name\": \"Ignatius\",\n" +
            "\t\t\"family_name\": \"Mikayla\",\n" +
            "\t\t\"age\": 69,\n" +
            "\t\t\"id\": \"AE490D74-B60D-ED12-2B2C-17DF7CE1E8B6\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Zoe\",\n" +
            "\t\t\"middle_name\": \"Jaquelyn\",\n" +
            "\t\t\"family_name\": \"Drew\",\n" +
            "\t\t\"age\": 65,\n" +
            "\t\t\"id\": \"A7C2D6AF-2851-547A-590A-4AB84FC8FB86\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Amaya\",\n" +
            "\t\t\"middle_name\": \"Pandora\",\n" +
            "\t\t\"family_name\": \"Tallulah\",\n" +
            "\t\t\"age\": 59,\n" +
            "\t\t\"id\": \"654204E4-3BE1-9C57-1082-7A12429AB245\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Xena\",\n" +
            "\t\t\"middle_name\": \"Dylan\",\n" +
            "\t\t\"family_name\": \"Rachel\",\n" +
            "\t\t\"age\": 6,\n" +
            "\t\t\"id\": \"1A78859A-36B8-8D00-55A2-03AC058DF59E\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Zorita\",\n" +
            "\t\t\"middle_name\": \"Lunea\",\n" +
            "\t\t\"family_name\": \"Freya\",\n" +
            "\t\t\"age\": 20,\n" +
            "\t\t\"id\": \"C53727BD-E453-EAA6-296D-52C0F09D27CE\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Cally\",\n" +
            "\t\t\"middle_name\": \"Chiquita\",\n" +
            "\t\t\"family_name\": \"Priscilla\",\n" +
            "\t\t\"age\": 18,\n" +
            "\t\t\"id\": \"6AD1A911-A13F-156C-865E-36E44F1C837B\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Kaye\",\n" +
            "\t\t\"middle_name\": \"Candice\",\n" +
            "\t\t\"family_name\": \"Rachel\",\n" +
            "\t\t\"age\": 33,\n" +
            "\t\t\"id\": \"368E410B-2442-BC8A-A208-1832F2043986\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Zia\",\n" +
            "\t\t\"middle_name\": \"Jelani\",\n" +
            "\t\t\"family_name\": \"Oliver\",\n" +
            "\t\t\"age\": 68,\n" +
            "\t\t\"id\": \"521472AF-FCFE-5937-B252-B777FD35BFB0\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Harlan\",\n" +
            "\t\t\"middle_name\": \"Andrew\",\n" +
            "\t\t\"family_name\": \"Caesar\",\n" +
            "\t\t\"age\": 18,\n" +
            "\t\t\"id\": \"A7DE47A3-C975-128E-C33B-D9BD2CFCF506\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Bernard\",\n" +
            "\t\t\"middle_name\": \"Vera\",\n" +
            "\t\t\"family_name\": \"Maxwell\",\n" +
            "\t\t\"age\": 64,\n" +
            "\t\t\"id\": \"61DF9C4E-3663-668A-FFBE-5266C86FE152\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Rudyard\",\n" +
            "\t\t\"middle_name\": \"Gay\",\n" +
            "\t\t\"family_name\": \"Christen\",\n" +
            "\t\t\"age\": 26,\n" +
            "\t\t\"id\": \"38541223-487E-A079-319B-6C620DEAD051\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Lavinia\",\n" +
            "\t\t\"middle_name\": \"Drake\",\n" +
            "\t\t\"family_name\": \"Ulla\",\n" +
            "\t\t\"age\": 29,\n" +
            "\t\t\"id\": \"29A48DC7-F219-926E-7016-7D334B3DF50F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Lydia\",\n" +
            "\t\t\"middle_name\": \"Ferris\",\n" +
            "\t\t\"family_name\": \"Sigourney\",\n" +
            "\t\t\"age\": 25,\n" +
            "\t\t\"id\": \"DA1A31E8-CD06-C7C4-6F2A-58C9D0813FA0\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Graham\",\n" +
            "\t\t\"middle_name\": \"Bianca\",\n" +
            "\t\t\"family_name\": \"Dara\",\n" +
            "\t\t\"age\": 31,\n" +
            "\t\t\"id\": \"D85270C3-9493-E72D-14C7-6937016FB27B\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Abra\",\n" +
            "\t\t\"middle_name\": \"Adena\",\n" +
            "\t\t\"family_name\": \"Stephen\",\n" +
            "\t\t\"age\": 77,\n" +
            "\t\t\"id\": \"9C3B5E20-5BF7-C674-2693-22F03BE623ED\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Gavin\",\n" +
            "\t\t\"middle_name\": \"Bruno\",\n" +
            "\t\t\"family_name\": \"Kibo\",\n" +
            "\t\t\"age\": 12,\n" +
            "\t\t\"id\": \"E238146A-09B1-ED30-402C-EA37D916A518\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Carter\",\n" +
            "\t\t\"middle_name\": \"Helen\",\n" +
            "\t\t\"family_name\": \"Uta\",\n" +
            "\t\t\"age\": 45,\n" +
            "\t\t\"id\": \"7B63B379-91FF-5395-19F1-2C386E813037\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Jenette\",\n" +
            "\t\t\"middle_name\": \"Lester\",\n" +
            "\t\t\"family_name\": \"Fleur\",\n" +
            "\t\t\"age\": 82,\n" +
            "\t\t\"id\": \"414924DB-A185-7BE3-1408-816AEDA3B481\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Laurel\",\n" +
            "\t\t\"middle_name\": \"Jemima\",\n" +
            "\t\t\"family_name\": \"Chelsea\",\n" +
            "\t\t\"age\": 91,\n" +
            "\t\t\"id\": \"AD584588-4003-8F37-88BA-455D4E42924C\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Althea\",\n" +
            "\t\t\"middle_name\": \"Kuame\",\n" +
            "\t\t\"family_name\": \"Aretha\",\n" +
            "\t\t\"age\": 19,\n" +
            "\t\t\"id\": \"6BB0BA49-48FE-42C9-8373-C235A2A35601\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Shay\",\n" +
            "\t\t\"middle_name\": \"Hilel\",\n" +
            "\t\t\"family_name\": \"Holmes\",\n" +
            "\t\t\"age\": 54,\n" +
            "\t\t\"id\": \"3C0976FB-049A-7837-36FF-810298FE3C1D\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Plato\",\n" +
            "\t\t\"middle_name\": \"Hermione\",\n" +
            "\t\t\"family_name\": \"Cheryl\",\n" +
            "\t\t\"age\": 58,\n" +
            "\t\t\"id\": \"86F1F369-3B62-CFC1-6860-95AF7E72DF9C\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Clare\",\n" +
            "\t\t\"middle_name\": \"Vance\",\n" +
            "\t\t\"family_name\": \"Kelly\",\n" +
            "\t\t\"age\": 25,\n" +
            "\t\t\"id\": \"088DF55F-8B29-D274-6000-B3D46FE0302D\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Jerome\",\n" +
            "\t\t\"middle_name\": \"Beverly\",\n" +
            "\t\t\"family_name\": \"Nora\",\n" +
            "\t\t\"age\": 83,\n" +
            "\t\t\"id\": \"ECCCE452-F8F9-577C-56EB-190DAB50A39F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Todd\",\n" +
            "\t\t\"middle_name\": \"Adele\",\n" +
            "\t\t\"family_name\": \"Xyla\",\n" +
            "\t\t\"age\": 34,\n" +
            "\t\t\"id\": \"2D35C95D-1069-908F-7755-CE94A53A8015\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Neville\",\n" +
            "\t\t\"middle_name\": \"Caleb\",\n" +
            "\t\t\"family_name\": \"Damon\",\n" +
            "\t\t\"age\": 45,\n" +
            "\t\t\"id\": \"DCAD2293-7B82-54DC-6E61-7842A527F5CF\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Mercedes\",\n" +
            "\t\t\"middle_name\": \"Igor\",\n" +
            "\t\t\"family_name\": \"Paula\",\n" +
            "\t\t\"age\": 44,\n" +
            "\t\t\"id\": \"55D1CFFF-5D52-7168-0E70-B8EF673766A1\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Karly\",\n" +
            "\t\t\"middle_name\": \"Hector\",\n" +
            "\t\t\"family_name\": \"Brennan\",\n" +
            "\t\t\"age\": 95,\n" +
            "\t\t\"id\": \"39F143B7-E85A-3830-C099-A9E6D6BA8D22\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Yael\",\n" +
            "\t\t\"middle_name\": \"Alec\",\n" +
            "\t\t\"family_name\": \"Whilemina\",\n" +
            "\t\t\"age\": 2,\n" +
            "\t\t\"id\": \"CE2BFC2D-0B5B-090E-EE90-059409963478\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Grady\",\n" +
            "\t\t\"middle_name\": \"Ryder\",\n" +
            "\t\t\"family_name\": \"Hashim\",\n" +
            "\t\t\"age\": 94,\n" +
            "\t\t\"id\": \"AF448AE7-6D7E-51C0-64D5-4C253D707A92\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Bethany\",\n" +
            "\t\t\"middle_name\": \"Chiquita\",\n" +
            "\t\t\"family_name\": \"Macy\",\n" +
            "\t\t\"age\": 38,\n" +
            "\t\t\"id\": \"CED91067-5B58-C305-C8D3-6F2F0985BE29\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Brenda\",\n" +
            "\t\t\"middle_name\": \"Emery\",\n" +
            "\t\t\"family_name\": \"Ray\",\n" +
            "\t\t\"age\": 45,\n" +
            "\t\t\"id\": \"6C977039-2E21-A862-2497-670EA2C19083\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Melinda\",\n" +
            "\t\t\"middle_name\": \"Raja\",\n" +
            "\t\t\"family_name\": \"Stacy\",\n" +
            "\t\t\"age\": 69,\n" +
            "\t\t\"id\": \"75C879F5-C06F-73EC-01A0-55FF1C979496\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Cailin\",\n" +
            "\t\t\"middle_name\": \"Cain\",\n" +
            "\t\t\"family_name\": \"Tobias\",\n" +
            "\t\t\"age\": 73,\n" +
            "\t\t\"id\": \"F8EE2DE1-D35D-233A-AADF-33FABCC6EE2D\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Fatima\",\n" +
            "\t\t\"middle_name\": \"Genevieve\",\n" +
            "\t\t\"family_name\": \"Donna\",\n" +
            "\t\t\"age\": 38,\n" +
            "\t\t\"id\": \"4611AF09-312E-8929-D99A-9ACC84579465\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Gillian\",\n" +
            "\t\t\"middle_name\": \"Emily\",\n" +
            "\t\t\"family_name\": \"Alfreda\",\n" +
            "\t\t\"age\": 32,\n" +
            "\t\t\"id\": \"AE3409E6-1AC6-9574-8DDF-341AE3D4B419\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Devin\",\n" +
            "\t\t\"middle_name\": \"Orlando\",\n" +
            "\t\t\"family_name\": \"Lisandra\",\n" +
            "\t\t\"age\": 66,\n" +
            "\t\t\"id\": \"D660E922-36BF-5566-B188-DDED58829BD2\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Cullen\",\n" +
            "\t\t\"middle_name\": \"Zane\",\n" +
            "\t\t\"family_name\": \"Portia\",\n" +
            "\t\t\"age\": 59,\n" +
            "\t\t\"id\": \"04AE78DB-6289-2F49-449B-1349ED4D90DA\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Nola\",\n" +
            "\t\t\"middle_name\": \"Farrah\",\n" +
            "\t\t\"family_name\": \"Stuart\",\n" +
            "\t\t\"age\": 9,\n" +
            "\t\t\"id\": \"F4712FEB-3F75-FFEB-48CC-A38ADE29D82E\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Marvin\",\n" +
            "\t\t\"middle_name\": \"Ross\",\n" +
            "\t\t\"family_name\": \"Nola\",\n" +
            "\t\t\"age\": 70,\n" +
            "\t\t\"id\": \"ED94BBBA-2DFF-F982-BB83-2303E351EBB0\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Cody\",\n" +
            "\t\t\"middle_name\": \"Yolanda\",\n" +
            "\t\t\"family_name\": \"Travis\",\n" +
            "\t\t\"age\": 42,\n" +
            "\t\t\"id\": \"CEFE73D0-CC0E-313C-0E76-4A2EB0006D13\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Madonna\",\n" +
            "\t\t\"middle_name\": \"Sonia\",\n" +
            "\t\t\"family_name\": \"Candice\",\n" +
            "\t\t\"age\": 47,\n" +
            "\t\t\"id\": \"570C34CD-E8B3-C344-1FA1-9FA14DDECD71\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Daphne\",\n" +
            "\t\t\"middle_name\": \"Shelly\",\n" +
            "\t\t\"family_name\": \"Charles\",\n" +
            "\t\t\"age\": 29,\n" +
            "\t\t\"id\": \"A04A5151-222C-C7BE-4917-B3EADAEED34A\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Irma\",\n" +
            "\t\t\"middle_name\": \"Thane\",\n" +
            "\t\t\"family_name\": \"Vaughan\",\n" +
            "\t\t\"age\": 72,\n" +
            "\t\t\"id\": \"66016915-A6C7-2DBA-00A3-139C90AC1DCE\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Kadeem\",\n" +
            "\t\t\"middle_name\": \"Justin\",\n" +
            "\t\t\"family_name\": \"Echo\",\n" +
            "\t\t\"age\": 32,\n" +
            "\t\t\"id\": \"9459182B-14D0-EB3C-D5AF-300B317F7780\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Nyssa\",\n" +
            "\t\t\"middle_name\": \"Paul\",\n" +
            "\t\t\"family_name\": \"Gareth\",\n" +
            "\t\t\"age\": 26,\n" +
            "\t\t\"id\": \"264086FA-E8BE-6407-E460-9D88CAFB5C14\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Adena\",\n" +
            "\t\t\"middle_name\": \"Kiayada\",\n" +
            "\t\t\"family_name\": \"Tyrone\",\n" +
            "\t\t\"age\": 82,\n" +
            "\t\t\"id\": \"83EC431D-0EEF-5AAD-28E3-6DA533E5BB0E\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Yardley\",\n" +
            "\t\t\"middle_name\": \"Lila\",\n" +
            "\t\t\"family_name\": \"Amos\",\n" +
            "\t\t\"age\": 31,\n" +
            "\t\t\"id\": \"F13FA1C2-F7D7-43B0-EFD9-DC46A435478F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Remedios\",\n" +
            "\t\t\"middle_name\": \"Aurelia\",\n" +
            "\t\t\"family_name\": \"Yen\",\n" +
            "\t\t\"age\": 59,\n" +
            "\t\t\"id\": \"70E5DC81-0C49-5294-89FD-93EA0DC1EB6F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Lev\",\n" +
            "\t\t\"middle_name\": \"Samantha\",\n" +
            "\t\t\"family_name\": \"Donovan\",\n" +
            "\t\t\"age\": 29,\n" +
            "\t\t\"id\": \"75E03382-2BC6-0832-D995-9AD1FAC82B4B\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Hiram\",\n" +
            "\t\t\"middle_name\": \"Urielle\",\n" +
            "\t\t\"family_name\": \"Justine\",\n" +
            "\t\t\"age\": 2,\n" +
            "\t\t\"id\": \"1A75337D-CE0C-4123-A102-D9B7E5C86BDD\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Emmanuel\",\n" +
            "\t\t\"middle_name\": \"Gretchen\",\n" +
            "\t\t\"family_name\": \"Winifred\",\n" +
            "\t\t\"age\": 82,\n" +
            "\t\t\"id\": \"FB4F140B-CD7E-C92C-88E4-C8C16D8244D8\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Cynthia\",\n" +
            "\t\t\"middle_name\": \"Cathleen\",\n" +
            "\t\t\"family_name\": \"Chaim\",\n" +
            "\t\t\"age\": 38,\n" +
            "\t\t\"id\": \"136BA264-2541-BACD-1028-A0CFCB27B78D\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Nissim\",\n" +
            "\t\t\"middle_name\": \"Myra\",\n" +
            "\t\t\"family_name\": \"Troy\",\n" +
            "\t\t\"age\": 97,\n" +
            "\t\t\"id\": \"E782B288-5CC0-E0D3-E3E9-80B1C171A828\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Camilla\",\n" +
            "\t\t\"middle_name\": \"Sonia\",\n" +
            "\t\t\"family_name\": \"Althea\",\n" +
            "\t\t\"age\": 88,\n" +
            "\t\t\"id\": \"02B77D9F-FEB6-DA65-5654-EB2B7B07CF0C\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Vance\",\n" +
            "\t\t\"middle_name\": \"Melinda\",\n" +
            "\t\t\"family_name\": \"Orlando\",\n" +
            "\t\t\"age\": 15,\n" +
            "\t\t\"id\": \"AE044BD2-3256-B630-FA02-32FBF4D5EAAE\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Tad\",\n" +
            "\t\t\"middle_name\": \"Brian\",\n" +
            "\t\t\"family_name\": \"Dylan\",\n" +
            "\t\t\"age\": 48,\n" +
            "\t\t\"id\": \"08B65C41-855B-82D9-B186-A61E148FFDF6\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Preston\",\n" +
            "\t\t\"middle_name\": \"Maite\",\n" +
            "\t\t\"family_name\": \"Carlos\",\n" +
            "\t\t\"age\": 95,\n" +
            "\t\t\"id\": \"967C84C9-6076-A160-2E36-A0FB7E61FE92\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Alyssa\",\n" +
            "\t\t\"middle_name\": \"Linus\",\n" +
            "\t\t\"family_name\": \"Lysandra\",\n" +
            "\t\t\"age\": 36,\n" +
            "\t\t\"id\": \"320332F2-0650-D76C-2751-989EDCF2E4C1\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Naomi\",\n" +
            "\t\t\"middle_name\": \"Jack\",\n" +
            "\t\t\"family_name\": \"Kelly\",\n" +
            "\t\t\"age\": 78,\n" +
            "\t\t\"id\": \"5D662486-978E-ABCF-F924-6C7FDD1522DE\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Irene\",\n" +
            "\t\t\"middle_name\": \"Shaine\",\n" +
            "\t\t\"family_name\": \"Ali\",\n" +
            "\t\t\"age\": 99,\n" +
            "\t\t\"id\": \"A1972370-EF6B-C276-8D0E-F05D35EDB0FB\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Maia\",\n" +
            "\t\t\"middle_name\": \"Keiko\",\n" +
            "\t\t\"family_name\": \"Quamar\",\n" +
            "\t\t\"age\": 66,\n" +
            "\t\t\"id\": \"346D2921-BA8A-2F32-EFAF-E69B558A48F9\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Chester\",\n" +
            "\t\t\"middle_name\": \"Graham\",\n" +
            "\t\t\"family_name\": \"Yoshi\",\n" +
            "\t\t\"age\": 46,\n" +
            "\t\t\"id\": \"107CD446-66A5-AB8E-7DBA-D1C95350C1FD\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Yen\",\n" +
            "\t\t\"middle_name\": \"Mercedes\",\n" +
            "\t\t\"family_name\": \"Clinton\",\n" +
            "\t\t\"age\": 85,\n" +
            "\t\t\"id\": \"02E9D268-0036-AE8D-75AE-E3137065D2B2\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Jada\",\n" +
            "\t\t\"middle_name\": \"Mollie\",\n" +
            "\t\t\"family_name\": \"Maxine\",\n" +
            "\t\t\"age\": 95,\n" +
            "\t\t\"id\": \"9F866295-113F-E033-448A-2629F9138508\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Julian\",\n" +
            "\t\t\"middle_name\": \"Kevyn\",\n" +
            "\t\t\"family_name\": \"Abdul\",\n" +
            "\t\t\"age\": 67,\n" +
            "\t\t\"id\": \"F7175A77-D9D8-BC58-1991-87CBDE178366\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Igor\",\n" +
            "\t\t\"middle_name\": \"Vanna\",\n" +
            "\t\t\"family_name\": \"Autumn\",\n" +
            "\t\t\"age\": 47,\n" +
            "\t\t\"id\": \"4D7BE430-3BC6-6DAF-772A-BE6548EA46B8\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Mara\",\n" +
            "\t\t\"middle_name\": \"Keely\",\n" +
            "\t\t\"family_name\": \"Leigh\",\n" +
            "\t\t\"age\": 28,\n" +
            "\t\t\"id\": \"F91227D9-2E67-D084-FA52-A015843646FE\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Colette\",\n" +
            "\t\t\"middle_name\": \"Sybil\",\n" +
            "\t\t\"family_name\": \"Norman\",\n" +
            "\t\t\"age\": 39,\n" +
            "\t\t\"id\": \"AE688C77-15CD-758F-92EC-3323275153BE\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Susan\",\n" +
            "\t\t\"middle_name\": \"Kuame\",\n" +
            "\t\t\"family_name\": \"Tiger\",\n" +
            "\t\t\"age\": 21,\n" +
            "\t\t\"id\": \"B4E89DB0-A9D7-15A7-F33E-7DF0475D2848\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Davis\",\n" +
            "\t\t\"middle_name\": \"Gil\",\n" +
            "\t\t\"family_name\": \"Tobias\",\n" +
            "\t\t\"age\": 63,\n" +
            "\t\t\"id\": \"3B2C8139-5F9D-22E4-8227-88CF3DE20C8D\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Thane\",\n" +
            "\t\t\"middle_name\": \"Ramona\",\n" +
            "\t\t\"family_name\": \"Francesca\",\n" +
            "\t\t\"age\": 7,\n" +
            "\t\t\"id\": \"D04AE836-7027-98A4-AD8F-1190E1A29285\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Sonia\",\n" +
            "\t\t\"middle_name\": \"Ivy\",\n" +
            "\t\t\"family_name\": \"Victoria\",\n" +
            "\t\t\"age\": 10,\n" +
            "\t\t\"id\": \"04B5EF5A-9707-71A7-C40D-9BE812DFCA25\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Ifeoma\",\n" +
            "\t\t\"middle_name\": \"Chastity\",\n" +
            "\t\t\"family_name\": \"Tanya\",\n" +
            "\t\t\"age\": 33,\n" +
            "\t\t\"id\": \"3E3606D3-2013-99C5-763C-DFAB83CC6D46\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Jessica\",\n" +
            "\t\t\"middle_name\": \"Rooney\",\n" +
            "\t\t\"family_name\": \"Peter\",\n" +
            "\t\t\"age\": 44,\n" +
            "\t\t\"id\": \"8845EB1B-7E78-B268-6DCB-5A75095F57CD\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Geraldine\",\n" +
            "\t\t\"middle_name\": \"Desiree\",\n" +
            "\t\t\"family_name\": \"Mohammad\",\n" +
            "\t\t\"age\": 49,\n" +
            "\t\t\"id\": \"62992902-74EC-7DD7-B10E-962AA6AE2EB7\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Alexander\",\n" +
            "\t\t\"middle_name\": \"Katell\",\n" +
            "\t\t\"family_name\": \"Raven\",\n" +
            "\t\t\"age\": 87,\n" +
            "\t\t\"id\": \"D2E231F4-0DC4-8BAB-C345-C3CCDB85589F\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Keaton\",\n" +
            "\t\t\"middle_name\": \"Leo\",\n" +
            "\t\t\"family_name\": \"Rhoda\",\n" +
            "\t\t\"age\": 11,\n" +
            "\t\t\"id\": \"903887FC-30CA-41A5-31A0-7435760D3077\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Nash\",\n" +
            "\t\t\"middle_name\": \"Wanda\",\n" +
            "\t\t\"family_name\": \"Lacota\",\n" +
            "\t\t\"age\": 33,\n" +
            "\t\t\"id\": \"C07D61FF-591C-0C87-C31C-C24F06145C05\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 1\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Stephen\",\n" +
            "\t\t\"middle_name\": \"Olivia\",\n" +
            "\t\t\"family_name\": \"Kiayada\",\n" +
            "\t\t\"age\": 25,\n" +
            "\t\t\"id\": \"51283E11-7B71-57AE-D48E-3B360D3F9897\",\n" +
            "\t\t\"gender\": \"m\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"given_name\": \"Edan\",\n" +
            "\t\t\"middle_name\": \"Nissim\",\n" +
            "\t\t\"family_name\": \"Malik\",\n" +
            "\t\t\"age\": 50,\n" +
            "\t\t\"id\": \"0AB99C99-797B-E57E-DEFB-F4D6A739393E\",\n" +
            "\t\t\"gender\": \"f\",\n" +
            "\t\t\"active_visit\": 0\n" +
            "\t}\n" +
            "]";

    public Patient() {
        try {
            patients = new JSONArray(patientsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Patient(String given_name, String middle_name, String family_name, String id, int age, char gender, int active_visit) {
        this.given_name = given_name;
        this.middle_name = middle_name;
        this.family_name = family_name;
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.active_visit = active_visit;
    }

    public Patient(JSONObject data) throws JSONException {
        this.given_name = data.getString("given_name");
        this.middle_name = data.getString("middle_name");
        this.family_name = data.getString("family_name");
        this.id = data.getString("id");
        this.age = data.getInt("age");
        this.gender = data.getString("gender").charAt(0);
        this.active_visit = data.getInt("active_visit");    }


    public Patient newPatient(String given_name, String middle_name, String family_name, String id, int age, char gender, int active_visit) {
        return new Patient(given_name, middle_name, family_name, id, age, gender, active_visit);
    }


    public JSONArray getPatients() {
        return patients;
    }

    public Patient getPatient(String patientId) throws JSONException {
        Patient patient = null;
        for (int index = 0; index < patients.length(); index++) {
            JSONObject p = (JSONObject) patients.get(index);
            if (p.get("id").equals(patientId)) {
                patient = new Patient((JSONObject) patients.get(index));
                break;
            }
        }
        return patient;
    }
}

