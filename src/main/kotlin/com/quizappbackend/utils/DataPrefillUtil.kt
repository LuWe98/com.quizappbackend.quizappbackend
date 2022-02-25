package com.quizappbackend.utils

import com.quizappbackend.extensions.generateHash
import com.quizappbackend.model.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.mongodb.documents.MongoFaculty
import com.quizappbackend.model.mongodb.documents.User
import com.quizappbackend.model.mongodb.properties.Degree.BACHELOR
import com.quizappbackend.model.mongodb.properties.Degree.MASTER
import com.quizappbackend.model.mongodb.properties.Role.ADMIN
import com.quizappbackend.model.mongodb.properties.Role.CREATOR

object DataPrefillUtil {

    private const val INITIAL_CREATOR_PASSWORD = "178Abtal!"

    val facultiesAndCoursesOfStudies get() : Pair<List<MongoFaculty>, List<MongoCourseOfStudies>> = run {
        val facultyDM = MongoFaculty(abbreviation = "DM", name = "Digitale Medien")

        val dmMIB = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "MIB", name = "Medieninformatik", degree = BACHELOR)
        val dmOMB = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "OMB", name = "Online Medien", degree = BACHELOR)
        val dmMKB = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "MKB", name = "Medienkonzeption", degree = BACHELOR)
        val dmMUM = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "MUM", name = "Music Design", degree = MASTER)
        val dmDIM = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "DIM", name = "Design Interaktiver Medien", degree = MASTER)
        val dmMIM = MongoCourseOfStudies(facultyIds = listOf(facultyDM.id), abbreviation = "MIM", name = "Medieninformatik", degree = MASTER)
        val cosListDM = listOf(dmMIB, dmOMB, dmMKB, dmMUM, dmDIM, dmMIM)


        val facultyGSG = MongoFaculty(abbreviation = "GSG", name = "Gesundheit, Sicherheit, Gesellschaft")

        val gsgAGW = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "AGW", name = "Angewandte Gesundheitswissenschaften", degree = BACHELOR)
        val gsgHW = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "HW", name = "Hebammenwissenschaft", degree = BACHELOR)
        val gsgPT = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "PT", name = "Physiotherapie", degree = BACHELOR)
        val gsgSSB = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "SSB", name = "Security and Safety Engineering", degree = BACHELOR)
        val gsgAGF = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "AGF", name = "Angewandte Gesundheitsförderung", degree = MASTER)
        val gsgIGF = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "IGF", name = "Interdisziplinäre Gesundheitsförderung", degree = MASTER)
        val gsgRIW = MongoCourseOfStudies(facultyIds = listOf(facultyGSG.id), abbreviation = "RIW", name = "Risikoingenieurwesen", degree = MASTER)
        val cosListGSG = listOf(gsgAGW, gsgHW, gsgPT, gsgSSB, gsgAGF, gsgIGF, gsgRIW)


        val facultyIN = MongoFaculty(abbreviation = "IN", name = "Informatik")

        val inAIN = MongoCourseOfStudies(facultyIds = listOf(facultyIN.id), abbreviation = "AIN", name = "Allgemeine Informatik", degree = BACHELOR)
        val inITP = MongoCourseOfStudies(facultyIds = listOf(facultyIN.id), abbreviation = "ITP", name = "IT-Produktmanagement", degree = BACHELOR)
        val inSPB = MongoCourseOfStudies(facultyIds = listOf(facultyIN.id), abbreviation = "SPB", name = "Software Produktmanagement", degree = BACHELOR)
        val inINM = MongoCourseOfStudies(facultyIds = listOf(facultyIN.id), abbreviation = "INM", name = "Informatik", degree = MASTER)
        val inMOS = MongoCourseOfStudies(facultyIds = listOf(facultyIN.id), abbreviation = "MOS", name = "Mobile Systeme", degree = MASTER)
        val cosListIN = listOf(inAIN, inITP, inSPB, inINM, inMOS)


        val facultyITE = MongoFaculty(abbreviation = "ITE", name = "Industrial Technologies")

        val iteOT = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "OT", name = "Studienmodell Orientierung Technik", degree = BACHELOR)
        val iteIMT = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "IMT", name = "Industrial MedTec", degree = BACHELOR)
        val iteIAM = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "IAM", name = "Industrial Automation and Mechatronics", degree = BACHELOR)
        val iteISD = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "ISD", name = "Industrial Systems Design", degree = BACHELOR)
        val iteIMF = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "IMF", name = "Industrial Manufactoring", degree = BACHELOR)
        val iteIME = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "IME", name = "Industrial Materials Engineering", degree = BACHELOR)
        val iteIP = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "IP", name = "Ingenieurpsychologie", degree = BACHELOR)
        val iteMDP = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "MDP", name = "Mechatronik und Digitale Produktion", degree = BACHELOR)
        val iteMTE =
            MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "MTE", name = "Medizintechnik - Technologien und Entwicklungsprozesse", degree = BACHELOR)
        val iteWFT = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "WFT", name = "Werkstoff- und Fertigungstechnik", degree = BACHELOR)
        val iteAMW = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "AMW", name = "Angewandte Materialwissenschaften", degree = MASTER)
        val iteHF = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "HF", name = "Human Factors", degree = MASTER)
        val iteMES = MongoCourseOfStudies(facultyIds = listOf(facultyITE.id), abbreviation = "MES", name = "Mechatronische Systeme", degree = MASTER)
        val cosListITE = listOf(iteOT, iteIMT, iteIAM, iteISD, iteIMF, iteIME, iteIP, iteMDP, iteMTE, iteWFT, iteAMW, iteHF, iteMES)


        val facultyMLS = MongoFaculty(abbreviation = "MLS", name = "Medical and Life Sciences")

        val mlsANB = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "ANB", name = "Angewandte Biologie", degree = BACHELOR)
        val mlsBPT = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "BPT", name = "Bio- und Prozesstechnologie", degree = BACHELOR)
        val mlsMTZ = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "MTZ", name = "Molekulare und Technische Medizin", degree = BACHELOR)
        val mlsNBT = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "NBT", name = "Nachhaltige Bioprozesstechnik", degree = MASTER)
        val mlsMDT = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "MDT", name = "Medical Diagnostic Technologies", degree = MASTER)
        val mlsPMD = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "PMD", name = "Precision Medicine Diagnostics", degree = MASTER)
        val mlsTP = MongoCourseOfStudies(facultyIds = listOf(facultyMLS.id), abbreviation = "TP", name = "Technical Physician", degree = MASTER)
        val cosListMLS = listOf(mlsANB, mlsBPT, mlsMTZ, mlsNBT, mlsMDT, mlsPMD, mlsTP)


        val facultyMME = MongoFaculty(abbreviation = "MME", name = "Mechanical and Medical Engineering")

        val mmeELA = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "ELA", name = "Elektrotechnik in Anwendung", degree = BACHELOR)
        val mmeMM = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "MM", name = "Maschinenbau und Mechatronik", degree = BACHELOR)
        val mmeMEB = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "MEB", name = "Medical Engineering", degree = BACHELOR)
        val mmeMKT = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "MKT", name = "Medizintechnik - Klinische Technologien", degree = BACHELOR)
        val mmeICS = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "ICS", name = "Information Communication Systems", degree = BACHELOR)
        val mmeAPE = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "APE", name = "Advanced Precision Engineering", degree = MASTER)
        val mmeBME = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "BME", name = "Biomedical Engineering", degree = MASTER)
        val mmeMZT = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "MZT", name = "Mikromedizin", degree = MASTER)
        val mmePMM = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "PMM", name = "Precision Manufactoring and Management", degree = MASTER)
        val mmeSMA = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id), abbreviation = "SMA", name = "Smart Systems", degree = MASTER)
        val cosListMME = listOf(mmeELA, mmeMM, mmeMEB, mmeMKT, mmeICS, mmeAPE, mmeBME, mmeMZT, mmePMM, mmeSMA)


        val facultyW = MongoFaculty(abbreviation = "W", name = "Wirtschaft")

        val wBMP = MongoCourseOfStudies(facultyIds = listOf(facultyW.id), abbreviation = "BMP", name = "Business Management and Psychology", degree = BACHELOR)
        val wIBW = MongoCourseOfStudies(facultyIds = listOf(facultyW.id), abbreviation = "IBW", name = "Internationale Betriebswirtschaft", degree = BACHELOR)
        val wIBM = MongoCourseOfStudies(facultyIds = listOf(facultyW.id), abbreviation = "IBM", name = "International Business Management", degree = BACHELOR)
        val wIMM = MongoCourseOfStudies(facultyIds = listOf(facultyW.id), abbreviation = "IMM", name = "International Management", degree = MASTER)
        val wMBA = MongoCourseOfStudies(facultyIds = listOf(facultyW.id), abbreviation = "MBA", name = "International Business Management", degree = MASTER)
        val cosListW = listOf(wBMP, wIBW, wIBM, wIMM, wMBA)


        val mme_w_IEB = MongoCourseOfStudies(facultyIds = listOf(facultyMME.id, facultyW.id), abbreviation = "IEB", name = "International Engineering", degree = BACHELOR)
        val mme_w_CosList = listOf(mme_w_IEB)


        val facultyWI = MongoFaculty(abbreviation = "WI", name = "Wirtschaftsinformatik")

        val wiIBS = MongoCourseOfStudies(facultyIds = listOf(facultyWI.id), abbreviation = "IBS", name = "International Business Information Systems", degree = BACHELOR)
        val wiWIB = MongoCourseOfStudies(facultyIds = listOf(facultyWI.id), abbreviation = "WIB", name = "Wirtschaftsinformatik", degree = BACHELOR)
        val wiWNB = MongoCourseOfStudies(facultyIds = listOf(facultyWI.id), abbreviation = "WNB", name = "Wirtschaftsnetze eBusiness", degree = BACHELOR)
        val wiBAM = MongoCourseOfStudies(facultyIds = listOf(facultyWI.id), abbreviation = "BAM", name = "Business Application Architecture", degree = MASTER)
        val wiBCM = MongoCourseOfStudies(facultyIds = listOf(facultyWI.id), abbreviation = "BCM", name = "Business Consulting", degree = MASTER)
        val cosListWI = listOf(wiIBS, wiWIB, wiWNB, wiBAM, wiBCM)


        val facultyWING = MongoFaculty(abbreviation = "WING", name = "Wirtschaftsingenieurwesen")

        val wingWIS = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "WIS", name = "Industrial Solutions Management", degree = BACHELOR)
        val wingSMB = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "SMB", name = "Service Management", degree = BACHELOR)
        val wingMVB = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "MVB", name = "Marketing und Vertrieb", degree = BACHELOR)
        val wingPEB = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "PEB", name = "Product Engineering", degree = BACHELOR)
        val wingWPI = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "WPI", name = "Product Innovation", degree = MASTER)
        val wingSEM = MongoCourseOfStudies(facultyIds = listOf(facultyWING.id), abbreviation = "SEM", name = "Sales & Service Engineering", degree = MASTER)
        val cosListWING = listOf(wingWIS, wingSMB, wingMVB, wingPEB, wingWPI, wingSEM)


        val faculties = listOf(facultyDM, facultyGSG, facultyIN, facultyITE, facultyMLS, facultyMME, facultyW, facultyWI, facultyWING)

        val coursesOfStudies = listOf(
            cosListDM,
            cosListGSG,
            cosListIN,
            cosListITE,
            cosListMLS,
            cosListMME,
            cosListW,
            mme_w_CosList,
            cosListWI,
            cosListWING
        ).flatten()

        return Pair(faculties, coursesOfStudies)
    }

    val professorList get(): List<User> = run {
        val digitaleMedienProfs = listOf(
            User(name = "Prof. Martin Aichele", role = CREATOR),
            User(name = "Prof. Dr. Jürgen Anders", role = CREATOR),
            User(name = "Prof. Dr. Jasmin Baumann", role = CREATOR),
            User(name = "Prof. Jirka Dell ´ Oro -Friedl", role = CREATOR),
            User(name = "Prof. Dr. Ullrich Dittler", role = CREATOR),
            User(name = "Prof. Dr. Dirk Eisenbiegler", role = CREATOR),
            User(name = "Prof. Christian Fries", role = CREATOR),
            User(name = "Prof. Dr. Miguel Garcia Gonzalez", role = CREATOR),
            User(name = "Prof. Dr. Uwe Hahne", role = CREATOR),
            User(name = "Prof. Dr. Stephanie Heintz", role = CREATOR),
            User(name = "Prof. Nikolaus Hottong", role = CREATOR),
            User(name = "Prof. Thomas Krach", role = CREATOR),
            User(name = "Prof. Florian Käppler", role = CREATOR),
            User(name = "Prof. Dr. Ruxandra Lasowski", role = CREATOR),
            User(name = "Prof. Christoph Müller", role = CREATOR),
            User(name = "Prof. Dr. Gotthard Pietsch", role = CREATOR),
            User(name = "Prof. Dr. Gabriel Rausch", role = CREATOR),
            User(name = "Prof. Matthias Reusch", role = CREATOR),
            User(name = "Prof. Regina Reusch", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Thomas Schneider", role = CREATOR),
            User(name = "Prof. Dr. Norbert Schnell", role = CREATOR),
            User(name = "Prof. Dr. Christoph Zydorek", role = CREATOR),
        )

        val gesundheitSicherheitGesellschaftProfs = listOf(
            User(name = "Prof. Dr. Angela Dieterich", role = CREATOR),
            User(name = "Prof. Dr. Klaus Grimm", role = CREATOR),
            User(name = "Prof. Dr. Dirk Koschützki", role = CREATOR),
            User(name = "Prof. Dr. Thilo Kromer", role = CREATOR),
            User(name = "Prof. Dr. Christophe Kunze", role = CREATOR),
            User(name = "Prof. Dr. Peter König", role = CREATOR),
            User(name = "Prof. Dr. Helmut Körber", role = CREATOR),
            User(name = "Prof. Dr. Stephan Lambotte", role = CREATOR),
            User(name = "Prof. Dr. Melvin Mohokum", role = CREATOR),
            User(name = "Prof. Dr. Hanna Niemann", role = CREATOR),
            User(name = "Prof. Dr. Sabine Prys", role = CREATOR),
            User(name = "Prof. Dr. habil. Birgit Reime", role = CREATOR),
            User(name = "Prof. Dr. Robert Richter", role = CREATOR),
            User(name = "Prof. Dr. Werner Riedel", role = CREATOR),
            User(name = "Prof. Dr. med. Kai Röcker", role = CREATOR),
            User(name = "Prof. Dr. Erwin Scherfer", role = CREATOR),
            User(name = "Prof. Dr. Melanie Schnee", role = CREATOR),
            User(name = "Prof. Dr. Stefan Selke", role = CREATOR),
            User(name = "Prof. Dr. Katrin Skerl", role = CREATOR),
            User(name = "Prof. Dr. Kirsten Steinhausen", role = CREATOR),
            User(name = "Prof. Dr. Ludger Stienen", role = CREATOR),
            User(name = "Prof. Dr. Arno Weber", role = CREATOR),
            User(name = "Prof. Dr.- Ing . Ulrich Weber", role = CREATOR),
            User(name = "Prof. Dr. Christian Weidmann", role = CREATOR)
        )

        val industrialTechnologiesProfs = listOf(
            User(name = "Prof. Dr. rer. nat. Frank Allmendinger", role = CREATOR),
            User(name = "Prof. Dr.-Ing . Peter Anders", role = CREATOR),
            User(name = "Prof. Dr.-Ing . Erwin Bürk", role = CREATOR),
            User(name = "Prof. Dr. Michael D 'Agosto", role = CREATOR),
            User(name = "Prof. Dr. Jens Deppler", role = CREATOR),
            User(name = "Prof. Dr. Sebastian Dörn", role = CREATOR),
            User(name = "Prof. Dr. Mike Fornefett", role = CREATOR),
            User(name = "Prof. Dr. Ulrich Gloistein", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Andreas Gollwitzer", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Kurt Greinwald", role = CREATOR),
            User(name = "Prof. Dr. Griselda Maria Guidoni", role = CREATOR),
            User(name = "Prof. Dr. Martin Haimerl", role = CREATOR),
            User(name = "Prof. Dr. Martin Heine", role = CREATOR),
            User(name = "Prof. Dr. Stephan Messner", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Hadi Mozaffari Jovein", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Stefan Pfeffer", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Siegfried Schmalzried", role = CREATOR),
            User(name = "Prof. Dr. Gerald Schmidt", role = CREATOR),
            User(name = "Prof. Dr. Albrecht Swietlik", role = CREATOR),
            User(name = "Prof. Dr. Verena Wagner-Hartl", role = CREATOR),
        )

        val informaticsProfs = listOf(
            User(name = "Prof. Dr. Stefan Betermieux", role = CREATOR),
            User(name = "Prof. Dr. Stefanie Betz", role = CREATOR),
            User(name = "Prof. Dr. Elmar Cochlovius", role = CREATOR),
            User(name = "Prof. Dr. Harald Gläser", role = CREATOR),
            User(name = "Prof. Dr. Bernhard Hollunder", role = CREATOR),
            User(name = "Prof. Dr. Achim P. Karduck", role = CREATOR),
            User(name = "Prof. Dr. Rainer Müller", role = CREATOR),
            User(name = "Prof. Dr. habil. Olaf Neiße", role = CREATOR),
            User(name = "Prof. Dr. Lothar Piepmeyer", role = CREATOR),
            User(name = "Prof. Dr. Mohsen Rezagholi", role = CREATOR),
            User(name = "Prof. Dr. Wolfgang Rülling", role = CREATOR),
            User(name = "Prof. Dr. Thomas Schake", role = CREATOR),
            User(name = "Prof. Dr. Maja Temerinac-Ott", role = CREATOR),
            User(name = "Prof. Dr. Steffen Thiel", role = CREATOR),
            User(name = "Prof. Dr. Richard Zahoransky", role = CREATOR)
        )

        val mechanicalAndMedicalEngineeringProfs = listOf(
            User(name = "Prof. Dr. Ekkehard Batzies", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Paola Belloni", role = CREATOR),
            User(name = "Prof. Dr. Christoph Benk", role = CREATOR),
            User(name = "Prof. Dr. Dirk Benyoucef", role = CREATOR),
            User(name = "Prof. Dr. Volker Bucher", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Ulrike Busolt", role = CREATOR),
            User(name = "Prof. Dr. med. Barbara Fink", role = CREATOR),
            User(name = "Prof. Dr. Jörg Friedrich", role = CREATOR),
            User(name = "Prof. Dr. med. Dipl.-Ing. (BA) Gerd Haimerl", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Edgar Jäger", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Gunter Ketterer", role = CREATOR),
            User(name = "Prof. Dr. sc. hum. Josef Kozak", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Rüdiger Kukral", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Barbara Lederle", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. habil. Margareta Müller", role = CREATOR),
            User(name = "Prof. Dr. Markus Niemann", role = CREATOR),
            User(name = "Prof. Dr. Dieter Schell", role = CREATOR),
            User(name = "Prof. Dr. Thomas Schiepp", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Helmut Schön", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Edgar Seemann", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Sliman Shaikheleid", role = CREATOR),
            User(name = "Prof. Dr. Richard Spiegelberg", role = CREATOR),
            User(name = "Prof. Dr. Ralf Trautwein", role = CREATOR),
            User(name = "Prof. Dr. Kirstin Tschan", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Bernhard Vondenbusch", role = CREATOR),
            User(name = "Prof. Dr. rer. pol. Barbara Winckler-Russ", role = CREATOR),
            User(name = "PD Dr. rer. nat. Dominik von Elverfeldt", role = CREATOR)
        )

        val medicalAndLifeSciencesProfs = listOf(
            User(name = "Prof. Dr. med. Meike Burger", role = CREATOR),
            User(name = "Prof. Dr. Holger Conzelmann", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Markus Egert", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Ulrike Fasol", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Andreas Fath", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Simon Hellstern", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Matthias Kohl", role = CREATOR),
            User(name = "Prof. Dr. med. Katja Kumle", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Tilmann Leverenz", role = CREATOR),
            User(name = "Prof. Dr. hum. biol. Ulrike Salat", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Magnus Schmidt", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Holger Schneider", role = CREATOR),
            User(name = "Dr. Sanaz Taromi", role = CREATOR)
        )

        val wirtschaftsinformatikProfs = listOf(
            User(name = "Prof. Dr. Marianne Andres", role = CREATOR),
            User(name = "Prof. Dr. -Ing. Jochen Baier", role = CREATOR),
            User(name = "Prof. Dr. Martin Buchheit", role = CREATOR),
            User(name = "Prof. Dr. Monika Frey-Luxemburger", role = CREATOR),
            User(name = "Prof. Gabriele Hecker", role = CREATOR),
            User(name = "Prof. Dr. Eduard Heindl", role = CREATOR),
            User(name = "Prof. Dr. Andreas Heß", role = CREATOR),
            User(name = "Prof. Dr. Martin Knahl", role = CREATOR),
            User(name = "Prof. Dr. Thomas Marx", role = CREATOR),
            User(name = "Prof. Dr. Peter Mattheis", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Stefan Noll", role = CREATOR),
            User(name = "Prof. Ph.D. Pavel Rawe", role = CREATOR),
            User(name = "Prof. Dr. Ulrich Roth", role = CREATOR),
            User(name = "Prof. Dr. Peter Schanbacher", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Ulf Schreier", role = CREATOR),
            User(name = "Prof. Dr. Guido Siestrup", role = CREATOR),
            User(name = "Prof. Dr. Oliver Taminé", role = ADMIN),
            User(name = "Prof. Dr. Jürgen Weiner", role = CREATOR),
            User(name = "Prof. Dr. Holger Ziekow", role = CREATOR)
        )

        val wirtschaftProfs = listOf(
            User(name = "Prof. Dr. Julika Baumann Montecinos", role = CREATOR),
            User(name = "Prof. Dr. oec. Niels Behrmann", role = CREATOR),
            User(name = "Prof. Dr. Daniel Cerquera", role = CREATOR),
            User(name = "Prof. Dr. rer. pol. Rütger Conzelmann", role = CREATOR),
            User(name = "Prof. Dr. Uwe Hack", role = CREATOR),
            User(name = "Prof. Dr. Markus Hoch", role = CREATOR),
            User(name = "Prof. Dr. iur. Gerrit Horstmeier M.M.", role = CREATOR),
            User(name = "Prof. Dr. Eva Kirner", role = CREATOR),
            User(name = "Prof. Dr. Frank Kramer", role = CREATOR),
            User(name = "Prof. Dr. oec. Michael Lederer", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Kai-Markus Müller", role = CREATOR),
            User(name = "Prof. Hardy Pfeiffer", role = CREATOR),
            User(name = "Prof. Dr. Marc Peter Radke", role = CREATOR),
            User(name = "Prof. Dr. Wolf-Dietrich Schneider", role = CREATOR),
            User(name = "Prof. Dr. Melanie Seemann", role = CREATOR),
            User(name = "Prof. Dr. Paul Taylor", role = CREATOR),
            User(name = "Prof. Dr. Armin Trost", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Jane Zima", role = CREATOR)
        )

        val wirtschaftsingenieurwesenProfs = listOf(
            User(name = "Prof. Dr.-Ing. habil Ute Diemar", role = CREATOR),
            User(name = "Prof. Dr. Michael Engler", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Hans-Georg Enkler", role = CREATOR),
            User(name = "Prof. Dr. rer. pol. Michael Gehrer", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Katja Gutsche", role = CREATOR),
            User(name = "Prof. Jörg Jacobi", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Steffen Jäger", role = CREATOR),
            User(name = "Prof. Dr. Ulrich Kallmann", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Hartmut Katz", role = CREATOR),
            User(name = "Prof. Dr. Uwe Kenntner", role = CREATOR),
            User(name = "Prof. Dr. rer. nat. Gerhard Kirchner", role = CREATOR),
            User(name = "Prof. Harald Kopp", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Christian Krause", role = CREATOR),
            User(name = "Prof. Lutz Leuendorf", role = CREATOR),
            User(name = "Prof. Dr. Steffen Munk", role = CREATOR),
            User(name = "Prof. Dr. Christa Pfeffer", role = CREATOR),
            User(name = "Prof. Dr. jur. Bernhard Plum", role = CREATOR),
            User(name = "Prof. Robert Schäflein-Armbruster", role = CREATOR),
            User(name = "Prof. Dr. sc. techn. Christoph Uhrhan", role = CREATOR),
            User(name = "Prof. Dr.-Ing. Christian van Husen", role = CREATOR)
        )

        listOf(
            digitaleMedienProfs,
            gesundheitSicherheitGesellschaftProfs,
            industrialTechnologiesProfs,
            informaticsProfs,
            mechanicalAndMedicalEngineeringProfs,
            medicalAndLifeSciencesProfs,
            wirtschaftsinformatikProfs,
            wirtschaftProfs,
            wirtschaftsingenieurwesenProfs,
        ).flatten().map {
            it.copy(password = "1234".generateHash())
        }
    }

    val professorListShortened get(): List<User> = run {
        val digitaleMedienProfs = listOf(
            User(name = "Prof. Martin Aichele", role = CREATOR),
            User(name = "Prof. Jürgen Anders", role = CREATOR),
            User(name = "Prof. Jasmin Baumann", role = CREATOR),
            User(name = "Prof. Jirka Dell ´ Oro -Friedl", role = CREATOR),
            User(name = "Prof. Ullrich Dittler", role = CREATOR),
            User(name = "Prof. Dirk Eisenbiegler", role = CREATOR),
            User(name = "Prof. Christian Fries", role = CREATOR),
            User(name = "Prof. Miguel Garcia Gonzalez", role = CREATOR),
            User(name = "Prof. Uwe Hahne", role = CREATOR),
            User(name = "Prof. Stephanie Heintz", role = CREATOR),
            User(name = "Prof. Nikolaus Hottong", role = CREATOR),
            User(name = "Prof. Thomas Krach", role = CREATOR),
            User(name = "Prof. Florian Käppler", role = CREATOR),
            User(name = "Prof. Ruxandra Lasowski", role = CREATOR),
            User(name = "Prof. Christoph Müller", role = CREATOR),
            User(name = "Prof. Gotthard Pietsch", role = CREATOR),
            User(name = "Prof. Gabriel Rausch", role = CREATOR),
            User(name = "Prof. Matthias Reusch", role = CREATOR),
            User(name = "Prof. Regina Reusch", role = CREATOR),
            User(name = "Prof. Thomas Schneider", role = CREATOR),
            User(name = "Prof. Norbert Schnell", role = CREATOR),
            User(name = "Prof. Christoph Zydorek", role = CREATOR),
        )

        val gesundheitSicherheitGesellschaftProfs = listOf(
            User(name = "Prof. Angela Dieterich", role = CREATOR),
            User(name = "Prof. Klaus Grimm", role = CREATOR),
            User(name = "Prof. Dirk Koschützki", role = CREATOR),
            User(name = "Prof. Thilo Kromer", role = CREATOR),
            User(name = "Prof. Christophe Kunze", role = CREATOR),
            User(name = "Prof. Peter König", role = CREATOR),
            User(name = "Prof. Helmut Körber", role = CREATOR),
            User(name = "Prof. Stephan Lambotte", role = CREATOR),
            User(name = "Prof. Melvin Mohokum", role = CREATOR),
            User(name = "Prof. Hanna Niemann", role = CREATOR),
            User(name = "Prof. Sabine Prys", role = CREATOR),
            User(name = "Prof. Birgit Reime", role = CREATOR),
            User(name = "Prof. Robert Richter", role = CREATOR),
            User(name = "Prof. Werner Riedel", role = CREATOR),
            User(name = "Prof. Kai Röcker", role = CREATOR),
            User(name = "Prof. Erwin Scherfer", role = CREATOR),
            User(name = "Prof. Melanie Schnee", role = CREATOR),
            User(name = "Prof. Stefan Selke", role = CREATOR),
            User(name = "Prof. Katrin Skerl", role = CREATOR),
            User(name = "Prof. Kirsten Steinhausen", role = CREATOR),
            User(name = "Prof. Ludger Stienen", role = CREATOR),
            User(name = "Prof. Arno Weber", role = CREATOR),
            User(name = "Prof. Ulrich Weber", role = CREATOR),
            User(name = "Prof. Christian Weidmann", role = CREATOR)
        )

        val industrialTechnologiesProfs = listOf(
            User(name = "Prof. Frank Allmendinger", role = CREATOR),
            User(name = "Prof. Peter Anders", role = CREATOR),
            User(name = "Prof. Erwin Bürk", role = CREATOR),
            User(name = "Prof. Michael D 'Agosto", role = CREATOR),
            User(name = "Prof. Jens Deppler", role = CREATOR),
            User(name = "Prof. Sebastian Dörn", role = CREATOR),
            User(name = "Prof. Mike Fornefett", role = CREATOR),
            User(name = "Prof. Ulrich Gloistein", role = CREATOR),
            User(name = "Prof. Andreas Gollwitzer", role = CREATOR),
            User(name = "Prof. Kurt Greinwald", role = CREATOR),
            User(name = "Prof. Griselda Maria Guidoni", role = CREATOR),
            User(name = "Prof. Martin Haimerl", role = CREATOR),
            User(name = "Prof. Martin Heine", role = CREATOR),
            User(name = "Prof. Stephan Messner", role = CREATOR),
            User(name = "Prof. Hadi Mozaffari Jovein", role = CREATOR),
            User(name = "Prof. Stefan Pfeffer", role = CREATOR),
            User(name = "Prof. Siegfried Schmalzried", role = CREATOR),
            User(name = "Prof. Gerald Schmidt", role = CREATOR),
            User(name = "Prof. Albrecht Swietlik", role = CREATOR),
            User(name = "Prof. Verena Wagner-Hartl", role = CREATOR),
        )

        val informaticsProfs = listOf(
            User(name = "Prof. Stefan Betermieux", role = CREATOR),
            User(name = "Prof. Stefanie Betz", role = CREATOR),
            User(name = "Prof. Elmar Cochlovius", role = CREATOR),
            User(name = "Prof. Harald Gläser", role = CREATOR),
            User(name = "Prof. Bernhard Hollunder", role = CREATOR),
            User(name = "Prof. Achim P. Karduck", role = CREATOR),
            User(name = "Prof. Rainer Müller", role = CREATOR),
            User(name = "Prof. Olaf Neiße", role = CREATOR),
            User(name = "Prof. Lothar Piepmeyer", role = CREATOR),
            User(name = "Prof. Mohsen Rezagholi", role = CREATOR),
            User(name = "Prof. Wolfgang Rülling", role = CREATOR),
            User(name = "Prof. Thomas Schake", role = CREATOR),
            User(name = "Prof. Maja Temerinac-Ott", role = CREATOR),
            User(name = "Prof. Steffen Thiel", role = CREATOR),
            User(name = "Prof. Richard Zahoransky", role = CREATOR)
        )

        val mechanicalAndMedicalEngineeringProfs = listOf(
            User(name = "Prof. Ekkehard Batzies", role = CREATOR),
            User(name = "Prof. Paola Belloni", role = CREATOR),
            User(name = "Prof. Christoph Benk", role = CREATOR),
            User(name = "Prof. Dirk Benyoucef", role = CREATOR),
            User(name = "Prof. Volker Bucher", role = CREATOR),
            User(name = "Prof. Ulrike Busolt", role = CREATOR),
            User(name = "Prof. Barbara Fink", role = CREATOR),
            User(name = "Prof. Jörg Friedrich", role = CREATOR),
            User(name = "Prof. Gerd Haimerl", role = CREATOR),
            User(name = "Prof. Edgar Jäger", role = CREATOR),
            User(name = "Prof. Gunter Ketterer", role = CREATOR),
            User(name = "Prof. Josef Kozak", role = CREATOR),
            User(name = "Prof. Rüdiger Kukral", role = CREATOR),
            User(name = "Prof. Barbara Lederle", role = CREATOR),
            User(name = "Prof. Margareta Müller", role = CREATOR),
            User(name = "Prof. Markus Niemann", role = CREATOR),
            User(name = "Prof. Dieter Schell", role = CREATOR),
            User(name = "Prof. Thomas Schiepp", role = CREATOR),
            User(name = "Prof. Helmut Schön", role = CREATOR),
            User(name = "Prof. Edgar Seemann", role = CREATOR),
            User(name = "Prof. Sliman Shaikheleid", role = CREATOR),
            User(name = "Prof. Richard Spiegelberg", role = CREATOR),
            User(name = "Prof. Ralf Trautwein", role = CREATOR),
            User(name = "Prof. Kirstin Tschan", role = CREATOR),
            User(name = "Prof. Bernhard Vondenbusch", role = CREATOR),
            User(name = "Prof. Barbara Winckler-Russ", role = CREATOR),
            User(name = "PD Dominik von Elverfeldt", role = CREATOR)
        )

        val medicalAndLifeSciencesProfs = listOf(
            User(name = "Prof. Meike Burger", role = CREATOR),
            User(name = "Prof. Holger Conzelmann", role = CREATOR),
            User(name = "Prof. Markus Egert", role = CREATOR),
            User(name = "Prof. Ulrike Fasol", role = CREATOR),
            User(name = "Prof. Andreas Fath", role = CREATOR),
            User(name = "Prof. Simon Hellstern", role = CREATOR),
            User(name = "Prof. Matthias Kohl", role = CREATOR),
            User(name = "Prof. Katja Kumle", role = CREATOR),
            User(name = "Prof. Tilmann Leverenz", role = CREATOR),
            User(name = "Prof. Ulrike Salat", role = CREATOR),
            User(name = "Prof. Magnus Schmidt", role = CREATOR),
            User(name = "Prof. Holger Schneider", role = CREATOR),
            User(name = "Prof. Sanaz Taromi", role = CREATOR)
        )

        val wirtschaftsinformatikProfs = listOf(
            User(name = "Prof. Marianne Andres", role = CREATOR),
            User(name = "Prof. Jochen Baier", role = CREATOR),
            User(name = "Prof. Martin Buchheit", role = CREATOR),
            User(name = "Prof. Monika Frey-Luxemburger", role = CREATOR),
            User(name = "Prof. Gabriele Hecker", role = CREATOR),
            User(name = "Prof. Eduard Heindl", role = CREATOR),
            User(name = "Prof. Andreas Heß", role = CREATOR),
            User(name = "Prof. Martin Knahl", role = CREATOR),
            User(name = "Prof. Thomas Marx", role = CREATOR),
            User(name = "Prof. Peter Mattheis", role = CREATOR),
            User(name = "Prof. Stefan Noll", role = CREATOR),
            User(name = "Prof. Pavel Rawe", role = CREATOR),
            User(name = "Prof. Ulrich Roth", role = CREATOR),
            User(name = "Prof. Peter Schanbacher", role = CREATOR),
            User(name = "Prof. Ulf Schreier", role = CREATOR),
            User(name = "Prof. Guido Siestrup", role = CREATOR),
            User(name = "Prof. Oliver Taminé", role = ADMIN),
            User(name = "Prof. Jürgen Weiner", role = CREATOR),
            User(name = "Prof. Holger Ziekow", role = CREATOR)
        )

        val wirtschaftProfs = listOf(
            User(name = "Prof. Julika Baumann Montecinos", role = CREATOR),
            User(name = "Prof. Niels Behrmann", role = CREATOR),
            User(name = "Prof. Daniel Cerquera", role = CREATOR),
            User(name = "Prof. Rütger Conzelmann", role = CREATOR),
            User(name = "Prof. Uwe Hack", role = CREATOR),
            User(name = "Prof. Markus Hoch", role = CREATOR),
            User(name = "Prof. Gerrit Horstmeier M.M.", role = CREATOR),
            User(name = "Prof. Eva Kirner", role = CREATOR),
            User(name = "Prof. Frank Kramer", role = CREATOR),
            User(name = "Prof. Michael Lederer", role = CREATOR),
            User(name = "Prof. Kai-Markus Müller", role = CREATOR),
            User(name = "Prof. Hardy Pfeiffer", role = CREATOR),
            User(name = "Prof. Marc Peter Radke", role = CREATOR),
            User(name = "Prof. Wolf-Dietrich Schneider", role = CREATOR),
            User(name = "Prof. Melanie Seemann", role = CREATOR),
            User(name = "Prof. Paul Taylor", role = CREATOR),
            User(name = "Prof. Armin Trost", role = CREATOR),
            User(name = "Prof. Jane Zima", role = CREATOR)
        )

        val wirtschaftsingenieurwesenProfs = listOf(
            User(name = "Prof. Ute Diemar", role = CREATOR),
            User(name = "Prof. Michael Engler", role = CREATOR),
            User(name = "Prof. Hans-Georg Enkler", role = CREATOR),
            User(name = "Prof. Michael Gehrer", role = CREATOR),
            User(name = "Prof. Katja Gutsche", role = CREATOR),
            User(name = "Prof. Jörg Jacobi", role = CREATOR),
            User(name = "Prof. Steffen Jäger", role = CREATOR),
            User(name = "Prof. Ulrich Kallmann", role = CREATOR),
            User(name = "Prof. Hartmut Katz", role = CREATOR),
            User(name = "Prof. Uwe Kenntner", role = CREATOR),
            User(name = "Prof. Gerhard Kirchner", role = CREATOR),
            User(name = "Prof. Harald Kopp", role = CREATOR),
            User(name = "Prof. Christian Krause", role = CREATOR),
            User(name = "Prof. Lutz Leuendorf", role = CREATOR),
            User(name = "Prof. Steffen Munk", role = CREATOR),
            User(name = "Prof. Christa Pfeffer", role = CREATOR),
            User(name = "Prof. Bernhard Plum", role = CREATOR),
            User(name = "Prof. Robert Schäflein-Armbruster", role = CREATOR),
            User(name = "Prof. Christoph Uhrhan", role = CREATOR),
            User(name = "Prof. Christian van Husen", role = CREATOR)
        )

        listOf(
            digitaleMedienProfs,
            gesundheitSicherheitGesellschaftProfs,
            industrialTechnologiesProfs,
            informaticsProfs,
            mechanicalAndMedicalEngineeringProfs,
            medicalAndLifeSciencesProfs,
            wirtschaftsinformatikProfs,
            wirtschaftProfs,
            wirtschaftsingenieurwesenProfs,
        ).flatten().map { it.copy(password = INITIAL_CREATOR_PASSWORD.generateHash()) }
    }
}