package com.quizappbackend.utils

import com.quizappbackend.model.databases.Degree
import com.quizappbackend.model.databases.Degree.*
import com.quizappbackend.model.databases.mongodb.documents.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.MongoFaculty
import com.quizappbackend.model.databases.mongodb.documents.MongoSubject
import com.quizappbackend.model.databases.mongodb.documents.user.User
import com.quizappbackend.model.databases.mongodb.documents.user.Role.*
import java.io.File

object DataPrefillUtil {

    private const val CSV_DELIMITER = ";"
    private const val CSV_FACULTY_DELIMITER = ","


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

    val facultiesAndCoursesOfStudiesFiles
        get() = run {
            val faculties = File("src/main/resources/prefill/faculties.csv").readLines().map { csvLine ->
                csvLine.split(CSV_DELIMITER).let { row ->
                    println("FACULTIES: $row")
                    MongoFaculty(abbreviation = row[0], name = row[1])
                }
            }

            val coursesOfStudies = File("src/main/resources/prefill/courses_of_studies.csv").readLines(Charsets.UTF_8).map { csvLine ->
                csvLine.split(CSV_DELIMITER).let { row ->
                    val facultyIds = row[3].split(CSV_FACULTY_DELIMITER).mapNotNull { facultyAbbreviation ->
                        if(facultyAbbreviation == "DM") {
                            println("ABBREVVIATION: $facultyAbbreviation")
                            faculties.forEach {
                                println("TEST: ${it.abbreviation.replace("\\0", "") == "DM"}")
                                println("SAME? ${it.abbreviation} AND $facultyAbbreviation ? = ${facultyAbbreviation.trim() == it.abbreviation.trim()}")
                            }
                        }
                        faculties.firstOrNull { it.abbreviation == facultyAbbreviation }?.id
                    }
                    MongoCourseOfStudies(abbreviation = row[0], name = row[1], degree = Degree.valueOf(row[2]), facultyIds = facultyIds)
                }
            }

            Pair(faculties, coursesOfStudies)
        }


    val professorList get(): List<User> = run {
        val digitaleMedienProfs = listOf(
            User(userName = "Prof. Martin Aichele", role = CREATOR),
            User(userName = "Prof. Dr. Jürgen Anders", role = CREATOR),
            User(userName = "Prof. Dr. Jasmin Baumann", role = CREATOR),
            User(userName = "Prof. Jirka Dell ´ Oro -Friedl", role = CREATOR),
            User(userName = "Prof. Dr. Ullrich Dittler", role = CREATOR),
            User(userName = "Prof. Dr. Dirk Eisenbiegler", role = CREATOR),
            User(userName = "Prof. Christian Fries", role = CREATOR),
            User(userName = "Prof. Dr. Miguel Garcia Gonzalez", role = CREATOR),
            User(userName = "Prof. Dr. Uwe Hahne", role = CREATOR),
            User(userName = "Prof. Dr. Stephanie Heintz", role = CREATOR),
            User(userName = "Prof. Nikolaus Hottong", role = CREATOR),
            User(userName = "Prof. Thomas Krach", role = CREATOR),
            User(userName = "Prof. Florian Käppler", role = CREATOR),
            User(userName = "Prof. Dr. Ruxandra Lasowski", role = CREATOR),
            User(userName = "Prof. Christoph Müller", role = CREATOR),
            User(userName = "Prof. Dr. Gotthard Pietsch", role = CREATOR),
            User(userName = "Prof. Dr. Gabriel Rausch", role = CREATOR),
            User(userName = "Prof. Matthias Reusch", role = CREATOR),
            User(userName = "Prof. Regina Reusch", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Thomas Schneider", role = CREATOR),
            User(userName = "Prof. Dr. Norbert Schnell", role = CREATOR),
            User(userName = "Prof. Dr. Christoph Zydorek", role = CREATOR),
        )

        val gesundheitSicherheitGesellschaftProfs = listOf(
            User(userName = "Prof. Dr. Angela Dieterich", role = CREATOR),
            User(userName = "Prof. Dr. Klaus Grimm", role = CREATOR),
            User(userName = "Prof. Dr. Dirk Koschützki", role = CREATOR),
            User(userName = "Prof. Dr. Thilo Kromer", role = CREATOR),
            User(userName = "Prof. Dr. Christophe Kunze", role = CREATOR),
            User(userName = "Prof. Dr. Peter König", role = CREATOR),
            User(userName = "Prof. Dr. Helmut Körber", role = CREATOR),
            User(userName = "Prof. Dr. Stephan Lambotte", role = CREATOR),
            User(userName = "Prof. Dr. Melvin Mohokum", role = CREATOR),
            User(userName = "Prof. Dr. Hanna Niemann", role = CREATOR),
            User(userName = "Prof. Dr. Sabine Prys", role = CREATOR),
            User(userName = "Prof. Dr. habil. Birgit Reime", role = CREATOR),
            User(userName = "Prof. Dr. Robert Richter", role = CREATOR),
            User(userName = "Prof. Dr. Werner Riedel", role = CREATOR),
            User(userName = "Prof. Dr. med. Kai Röcker", role = CREATOR),
            User(userName = "Prof. Dr. Erwin Scherfer", role = CREATOR),
            User(userName = "Prof. Dr. Melanie Schnee", role = CREATOR),
            User(userName = "Prof. Dr. Stefan Selke", role = CREATOR),
            User(userName = "Prof. Dr. Katrin Skerl", role = CREATOR),
            User(userName = "Prof. Dr. Kirsten Steinhausen", role = CREATOR),
            User(userName = "Prof. Dr. Ludger Stienen", role = CREATOR),
            User(userName = "Prof. Dr. Arno Weber", role = CREATOR),
            User(userName = "Prof. Dr.- Ing . Ulrich Weber", role = CREATOR),
            User(userName = "Prof. Dr. Christian Weidmann", role = CREATOR)
        )

        val industrialTechnologiesProfs = listOf(
            User(userName = "Prof. Dr. rer. nat. Frank Allmendinger", role = CREATOR),
            User(userName = "Prof. Dr.-Ing . Peter Anders", role = CREATOR),
            User(userName = "Prof. Dr.-Ing . Erwin Bürk", role = CREATOR),
            User(userName = "Prof. Dr. Michael D 'Agosto", role = CREATOR),
            User(userName = "Prof. Dr. Jens Deppler", role = CREATOR),
            User(userName = "Prof. Dr. Sebastian Dörn", role = CREATOR),
            User(userName = "Prof. Dr. Mike Fornefett", role = CREATOR),
            User(userName = "Prof. Dr. Ulrich Gloistein", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Andreas Gollwitzer", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Kurt Greinwald", role = CREATOR),
            User(userName = "Prof. Dr. Griselda Maria Guidoni", role = CREATOR),
            User(userName = "Prof. Dr. Martin Haimerl", role = CREATOR),
            User(userName = "Prof. Dr. Martin Heine", role = CREATOR),
            User(userName = "Prof. Dr. Stephan Messner", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Hadi Mozaffari Jovein", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Stefan Pfeffer", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Siegfried Schmalzried", role = CREATOR),
            User(userName = "Prof. Dr. Gerald Schmidt", role = CREATOR),
            User(userName = "Prof. Dr. Albrecht Swietlik", role = CREATOR),
            User(userName = "Prof. Dr. Verena Wagner-Hartl", role = CREATOR),
        )

        val informaticsProfs = listOf(
            User(userName = "Prof. Dr. Stefan Betermieux", role = CREATOR),
            User(userName = "Prof. Dr. Stefanie Betz", role = CREATOR),
            User(userName = "Prof. Dr. Elmar Cochlovius", role = CREATOR),
            User(userName = "Prof. Dr. Harald Gläser", role = CREATOR),
            User(userName = "Prof. Dr. Bernhard Hollunder", role = CREATOR),
            User(userName = "Prof. Dr. Achim P. Karduck", role = CREATOR),
            User(userName = "Prof. Dr. Rainer Müller", role = CREATOR),
            User(userName = "Prof. Dr. habil. Olaf Neiße", role = CREATOR),
            User(userName = "Prof. Dr. Lothar Piepmeyer", role = CREATOR),
            User(userName = "Prof. Dr. Mohsen Rezagholi", role = CREATOR),
            User(userName = "Prof. Dr. Wolfgang Rülling", role = CREATOR),
            User(userName = "Prof. Dr. Thomas Schake", role = CREATOR),
            User(userName = "Prof. Dr. Maja Temerinac-Ott", role = CREATOR),
            User(userName = "Prof. Dr. Steffen Thiel", role = CREATOR),
            User(userName = "Prof. Dr. Richard Zahoransky", role = CREATOR)
        )

        val mechanicalAndMedicalEngineeringProfs = listOf(
            User(userName = "Prof. Dr. Ekkehard Batzies", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Paola Belloni", role = CREATOR),
            User(userName = "Prof. Dr. Christoph Benk", role = CREATOR),
            User(userName = "Prof. Dr. Dirk Benyoucef", role = CREATOR),
            User(userName = "Prof. Dr. Volker Bucher", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Ulrike Busolt", role = CREATOR),
            User(userName = "Prof. Dr. med. Barbara Fink", role = CREATOR),
            User(userName = "Prof. Dr. Jörg Friedrich", role = CREATOR),
            User(userName = "Prof. Dr. med. Dipl.-Ing. (BA) Gerd Haimerl", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Edgar Jäger", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Gunter Ketterer", role = CREATOR),
            User(userName = "Prof. Dr. sc. hum. Josef Kozak", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Rüdiger Kukral", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Barbara Lederle", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. habil. Margareta Müller", role = CREATOR),
            User(userName = "Prof. Dr. Markus Niemann", role = CREATOR),
            User(userName = "Prof. Dr. Dieter Schell", role = CREATOR),
            User(userName = "Prof. Dr. Thomas Schiepp", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Helmut Schön", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Edgar Seemann", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Sliman Shaikheleid", role = CREATOR),
            User(userName = "Prof. Dr. Richard Spiegelberg", role = CREATOR),
            User(userName = "Prof. Dr. Ralf Trautwein", role = CREATOR),
            User(userName = "Prof. Dr. Kirstin Tschan", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Bernhard Vondenbusch", role = CREATOR),
            User(userName = "Prof. Dr. rer. pol. Barbara Winckler-Russ", role = CREATOR),
            User(userName = "PD Dr. rer. nat. Dominik von Elverfeldt", role = CREATOR)
        )

        val medicalAndLifeSciencesProfs = listOf(
            User(userName = "Prof. Dr. med. Meike Burger", role = CREATOR),
            User(userName = "Prof. Dr. Holger Conzelmann", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Markus Egert", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Ulrike Fasol", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Andreas Fath", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Simon Hellstern", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Matthias Kohl", role = CREATOR),
            User(userName = "Prof. Dr. med. Katja Kumle", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Tilmann Leverenz", role = CREATOR),
            User(userName = "Prof. Dr. hum. biol. Ulrike Salat", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Magnus Schmidt", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Holger Schneider", role = CREATOR),
            User(userName = "Dr. Sanaz Taromi", role = CREATOR)
        )

        val wirtschaftsinformatikProfs = listOf(
            User(userName = "Prof. Dr. Marianne Andres", role = CREATOR),
            User(userName = "Prof. Dr. -Ing. Jochen Baier", role = CREATOR),
            User(userName = "Prof. Dr. Martin Buchheit", role = CREATOR),
            User(userName = "Prof. Dr. Monika Frey-Luxemburger", role = CREATOR),
            User(userName = "Prof. Gabriele Hecker", role = CREATOR),
            User(userName = "Prof. Dr. Eduard Heindl", role = CREATOR),
            User(userName = "Prof. Dr. Andreas Heß", role = CREATOR),
            User(userName = "Prof. Dr. Martin Knahl", role = CREATOR),
            User(userName = "Prof. Dr. Thomas Marx", role = CREATOR),
            User(userName = "Prof. Dr. Peter Mattheis", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Stefan Noll", role = CREATOR),
            User(userName = "Prof. Ph.D. Pavel Rawe", role = CREATOR),
            User(userName = "Prof. Dr. Ulrich Roth", role = CREATOR),
            User(userName = "Prof. Dr. Peter Schanbacher", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Ulf Schreier", role = CREATOR),
            User(userName = "Prof. Dr. Guido Siestrup", role = CREATOR),
            User(userName = "Prof. Dr. Oliver Taminé", role = ADMIN),
            User(userName = "Prof. Dr. Jürgen Weiner", role = CREATOR),
            User(userName = "Prof. Dr. Holger Ziekow", role = CREATOR)
        )

        val wirtschaftProfs = listOf(
            User(userName = "Prof. Dr. Julika Baumann Montecinos", role = CREATOR),
            User(userName = "Prof. Dr. oec. Niels Behrmann", role = CREATOR),
            User(userName = "Prof. Dr. Daniel Cerquera", role = CREATOR),
            User(userName = "Prof. Dr. rer. pol. Rütger Conzelmann", role = CREATOR),
            User(userName = "Prof. Dr. Uwe Hack", role = CREATOR),
            User(userName = "Prof. Dr. Markus Hoch", role = CREATOR),
            User(userName = "Prof. Dr. iur. Gerrit Horstmeier M.M.", role = CREATOR),
            User(userName = "Prof. Dr. Eva Kirner", role = CREATOR),
            User(userName = "Prof. Dr. Frank Kramer", role = CREATOR),
            User(userName = "Prof. Dr. oec. Michael Lederer", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Kai-Markus Müller", role = CREATOR),
            User(userName = "Prof. Hardy Pfeiffer", role = CREATOR),
            User(userName = "Prof. Dr. Marc Peter Radke", role = CREATOR),
            User(userName = "Prof. Dr. Wolf-Dietrich Schneider", role = CREATOR),
            User(userName = "Prof. Dr. Melanie Seemann", role = CREATOR),
            User(userName = "Prof. Dr. Paul Taylor", role = CREATOR),
            User(userName = "Prof. Dr. Armin Trost", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Jane Zima", role = CREATOR)
        )

        val wirtschaftsingenieurwesenProfs = listOf(
            User(userName = "Prof. Dr.-Ing. habil Ute Diemar", role = CREATOR),
            User(userName = "Prof. Dr. Michael Engler", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Hans-Georg Enkler", role = CREATOR),
            User(userName = "Prof. Dr. rer. pol. Michael Gehrer", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Katja Gutsche", role = CREATOR),
            User(userName = "Prof. Jörg Jacobi", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Steffen Jäger", role = CREATOR),
            User(userName = "Prof. Dr. Ulrich Kallmann", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Hartmut Katz", role = CREATOR),
            User(userName = "Prof. Dr. Uwe Kenntner", role = CREATOR),
            User(userName = "Prof. Dr. rer. nat. Gerhard Kirchner", role = CREATOR),
            User(userName = "Prof. Harald Kopp", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Christian Krause", role = CREATOR),
            User(userName = "Prof. Lutz Leuendorf", role = CREATOR),
            User(userName = "Prof. Dr. Steffen Munk", role = CREATOR),
            User(userName = "Prof. Dr. Christa Pfeffer", role = CREATOR),
            User(userName = "Prof. Dr. jur. Bernhard Plum", role = CREATOR),
            User(userName = "Prof. Robert Schäflein-Armbruster", role = CREATOR),
            User(userName = "Prof. Dr. sc. techn. Christoph Uhrhan", role = CREATOR),
            User(userName = "Prof. Dr.-Ing. Christian van Husen", role = CREATOR)
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

    val professorListFiles
        get() = run {
            File("src/main/resources/prefill/professors.txt").readLines(Charsets.UTF_8).map { professorName ->
                User(userName = professorName, password = "1234".generateHash(), role = if(professorName == "Prof. Dr. Oliver Taminé") ADMIN else CREATOR)
            }
        }


    val professorListShortened get(): List<User> = run {
        val digitaleMedienProfs = listOf(
            User(userName = "Prof. Martin Aichele", role = CREATOR),
            User(userName = "Prof. Jürgen Anders", role = CREATOR),
            User(userName = "Prof. Jasmin Baumann", role = CREATOR),
            User(userName = "Prof. Jirka Dell ´ Oro -Friedl", role = CREATOR),
            User(userName = "Prof. Ullrich Dittler", role = CREATOR),
            User(userName = "Prof. Dirk Eisenbiegler", role = CREATOR),
            User(userName = "Prof. Christian Fries", role = CREATOR),
            User(userName = "Prof. Miguel Garcia Gonzalez", role = CREATOR),
            User(userName = "Prof. Uwe Hahne", role = CREATOR),
            User(userName = "Prof. Stephanie Heintz", role = CREATOR),
            User(userName = "Prof. Nikolaus Hottong", role = CREATOR),
            User(userName = "Prof. Thomas Krach", role = CREATOR),
            User(userName = "Prof. Florian Käppler", role = CREATOR),
            User(userName = "Prof. Ruxandra Lasowski", role = CREATOR),
            User(userName = "Prof. Christoph Müller", role = CREATOR),
            User(userName = "Prof. Gotthard Pietsch", role = CREATOR),
            User(userName = "Prof. Gabriel Rausch", role = CREATOR),
            User(userName = "Prof. Matthias Reusch", role = CREATOR),
            User(userName = "Prof. Regina Reusch", role = CREATOR),
            User(userName = "Prof. Thomas Schneider", role = CREATOR),
            User(userName = "Prof. Norbert Schnell", role = CREATOR),
            User(userName = "Prof. Christoph Zydorek", role = CREATOR),
        )

        val gesundheitSicherheitGesellschaftProfs = listOf(
            User(userName = "Prof. Angela Dieterich", role = CREATOR),
            User(userName = "Prof. Klaus Grimm", role = CREATOR),
            User(userName = "Prof. Dirk Koschützki", role = CREATOR),
            User(userName = "Prof. Thilo Kromer", role = CREATOR),
            User(userName = "Prof. Christophe Kunze", role = CREATOR),
            User(userName = "Prof. Peter König", role = CREATOR),
            User(userName = "Prof. Helmut Körber", role = CREATOR),
            User(userName = "Prof. Stephan Lambotte", role = CREATOR),
            User(userName = "Prof. Melvin Mohokum", role = CREATOR),
            User(userName = "Prof. Hanna Niemann", role = CREATOR),
            User(userName = "Prof. Sabine Prys", role = CREATOR),
            User(userName = "Prof. Birgit Reime", role = CREATOR),
            User(userName = "Prof. Robert Richter", role = CREATOR),
            User(userName = "Prof. Werner Riedel", role = CREATOR),
            User(userName = "Prof. Kai Röcker", role = CREATOR),
            User(userName = "Prof. Erwin Scherfer", role = CREATOR),
            User(userName = "Prof. Melanie Schnee", role = CREATOR),
            User(userName = "Prof. Stefan Selke", role = CREATOR),
            User(userName = "Prof. Katrin Skerl", role = CREATOR),
            User(userName = "Prof. Kirsten Steinhausen", role = CREATOR),
            User(userName = "Prof. Ludger Stienen", role = CREATOR),
            User(userName = "Prof. Arno Weber", role = CREATOR),
            User(userName = "Prof. Ulrich Weber", role = CREATOR),
            User(userName = "Prof. Christian Weidmann", role = CREATOR)
        )

        val industrialTechnologiesProfs = listOf(
            User(userName = "Prof. Frank Allmendinger", role = CREATOR),
            User(userName = "Prof. Peter Anders", role = CREATOR),
            User(userName = "Prof. Erwin Bürk", role = CREATOR),
            User(userName = "Prof. Michael D 'Agosto", role = CREATOR),
            User(userName = "Prof. Jens Deppler", role = CREATOR),
            User(userName = "Prof. Sebastian Dörn", role = CREATOR),
            User(userName = "Prof. Mike Fornefett", role = CREATOR),
            User(userName = "Prof. Ulrich Gloistein", role = CREATOR),
            User(userName = "Prof. Andreas Gollwitzer", role = CREATOR),
            User(userName = "Prof. Kurt Greinwald", role = CREATOR),
            User(userName = "Prof. Griselda Maria Guidoni", role = CREATOR),
            User(userName = "Prof. Martin Haimerl", role = CREATOR),
            User(userName = "Prof. Martin Heine", role = CREATOR),
            User(userName = "Prof. Stephan Messner", role = CREATOR),
            User(userName = "Prof. Hadi Mozaffari Jovein", role = CREATOR),
            User(userName = "Prof. Stefan Pfeffer", role = CREATOR),
            User(userName = "Prof. Siegfried Schmalzried", role = CREATOR),
            User(userName = "Prof. Gerald Schmidt", role = CREATOR),
            User(userName = "Prof. Albrecht Swietlik", role = CREATOR),
            User(userName = "Prof. Verena Wagner-Hartl", role = CREATOR),
        )

        val informaticsProfs = listOf(
            User(userName = "Prof. Stefan Betermieux", role = CREATOR),
            User(userName = "Prof. Stefanie Betz", role = CREATOR),
            User(userName = "Prof. Elmar Cochlovius", role = CREATOR),
            User(userName = "Prof. Harald Gläser", role = CREATOR),
            User(userName = "Prof. Bernhard Hollunder", role = CREATOR),
            User(userName = "Prof. Achim P. Karduck", role = CREATOR),
            User(userName = "Prof. Rainer Müller", role = CREATOR),
            User(userName = "Prof. Olaf Neiße", role = CREATOR),
            User(userName = "Prof. Lothar Piepmeyer", role = CREATOR),
            User(userName = "Prof. Mohsen Rezagholi", role = CREATOR),
            User(userName = "Prof. Wolfgang Rülling", role = CREATOR),
            User(userName = "Prof. Thomas Schake", role = CREATOR),
            User(userName = "Prof. Maja Temerinac-Ott", role = CREATOR),
            User(userName = "Prof. Steffen Thiel", role = CREATOR),
            User(userName = "Prof. Richard Zahoransky", role = CREATOR)
        )

        val mechanicalAndMedicalEngineeringProfs = listOf(
            User(userName = "Prof. Ekkehard Batzies", role = CREATOR),
            User(userName = "Prof. Paola Belloni", role = CREATOR),
            User(userName = "Prof. Christoph Benk", role = CREATOR),
            User(userName = "Prof. Dirk Benyoucef", role = CREATOR),
            User(userName = "Prof. Volker Bucher", role = CREATOR),
            User(userName = "Prof. Ulrike Busolt", role = CREATOR),
            User(userName = "Prof. Barbara Fink", role = CREATOR),
            User(userName = "Prof. Jörg Friedrich", role = CREATOR),
            User(userName = "Prof. Gerd Haimerl", role = CREATOR),
            User(userName = "Prof. Edgar Jäger", role = CREATOR),
            User(userName = "Prof. Gunter Ketterer", role = CREATOR),
            User(userName = "Prof. Josef Kozak", role = CREATOR),
            User(userName = "Prof. Rüdiger Kukral", role = CREATOR),
            User(userName = "Prof. Barbara Lederle", role = CREATOR),
            User(userName = "Prof. Margareta Müller", role = CREATOR),
            User(userName = "Prof. Markus Niemann", role = CREATOR),
            User(userName = "Prof. Dieter Schell", role = CREATOR),
            User(userName = "Prof. Thomas Schiepp", role = CREATOR),
            User(userName = "Prof. Helmut Schön", role = CREATOR),
            User(userName = "Prof. Edgar Seemann", role = CREATOR),
            User(userName = "Prof. Sliman Shaikheleid", role = CREATOR),
            User(userName = "Prof. Richard Spiegelberg", role = CREATOR),
            User(userName = "Prof. Ralf Trautwein", role = CREATOR),
            User(userName = "Prof. Kirstin Tschan", role = CREATOR),
            User(userName = "Prof. Bernhard Vondenbusch", role = CREATOR),
            User(userName = "Prof. Barbara Winckler-Russ", role = CREATOR),
            User(userName = "PD Dominik von Elverfeldt", role = CREATOR)
        )

        val medicalAndLifeSciencesProfs = listOf(
            User(userName = "Prof. Meike Burger", role = CREATOR),
            User(userName = "Prof. Holger Conzelmann", role = CREATOR),
            User(userName = "Prof. Markus Egert", role = CREATOR),
            User(userName = "Prof. Ulrike Fasol", role = CREATOR),
            User(userName = "Prof. Andreas Fath", role = CREATOR),
            User(userName = "Prof. Simon Hellstern", role = CREATOR),
            User(userName = "Prof. Matthias Kohl", role = CREATOR),
            User(userName = "Prof. Katja Kumle", role = CREATOR),
            User(userName = "Prof. Tilmann Leverenz", role = CREATOR),
            User(userName = "Prof. Ulrike Salat", role = CREATOR),
            User(userName = "Prof. Magnus Schmidt", role = CREATOR),
            User(userName = "Prof. Holger Schneider", role = CREATOR),
            User(userName = "Prof. Sanaz Taromi", role = CREATOR)
        )

        val wirtschaftsinformatikProfs = listOf(
            User(userName = "Prof. Marianne Andres", role = CREATOR),
            User(userName = "Prof. Jochen Baier", role = CREATOR),
            User(userName = "Prof. Martin Buchheit", role = CREATOR),
            User(userName = "Prof. Monika Frey-Luxemburger", role = CREATOR),
            User(userName = "Prof. Gabriele Hecker", role = CREATOR),
            User(userName = "Prof. Eduard Heindl", role = CREATOR),
            User(userName = "Prof. Andreas Heß", role = CREATOR),
            User(userName = "Prof. Martin Knahl", role = CREATOR),
            User(userName = "Prof. Thomas Marx", role = CREATOR),
            User(userName = "Prof. Peter Mattheis", role = CREATOR),
            User(userName = "Prof. Stefan Noll", role = CREATOR),
            User(userName = "Prof. Pavel Rawe", role = CREATOR),
            User(userName = "Prof. Ulrich Roth", role = CREATOR),
            User(userName = "Prof. Peter Schanbacher", role = CREATOR),
            User(userName = "Prof. Ulf Schreier", role = CREATOR),
            User(userName = "Prof. Guido Siestrup", role = CREATOR),
            User(userName = "Prof. Oliver Taminé", role = ADMIN),
            User(userName = "Prof. Jürgen Weiner", role = CREATOR),
            User(userName = "Prof. Holger Ziekow", role = CREATOR)
        )

        val wirtschaftProfs = listOf(
            User(userName = "Prof. Julika Baumann Montecinos", role = CREATOR),
            User(userName = "Prof. Niels Behrmann", role = CREATOR),
            User(userName = "Prof. Daniel Cerquera", role = CREATOR),
            User(userName = "Prof. Rütger Conzelmann", role = CREATOR),
            User(userName = "Prof. Uwe Hack", role = CREATOR),
            User(userName = "Prof. Markus Hoch", role = CREATOR),
            User(userName = "Prof. Gerrit Horstmeier M.M.", role = CREATOR),
            User(userName = "Prof. Eva Kirner", role = CREATOR),
            User(userName = "Prof. Frank Kramer", role = CREATOR),
            User(userName = "Prof. Michael Lederer", role = CREATOR),
            User(userName = "Prof. Kai-Markus Müller", role = CREATOR),
            User(userName = "Prof. Hardy Pfeiffer", role = CREATOR),
            User(userName = "Prof. Marc Peter Radke", role = CREATOR),
            User(userName = "Prof. Wolf-Dietrich Schneider", role = CREATOR),
            User(userName = "Prof. Melanie Seemann", role = CREATOR),
            User(userName = "Prof. Paul Taylor", role = CREATOR),
            User(userName = "Prof. Armin Trost", role = CREATOR),
            User(userName = "Prof. Jane Zima", role = CREATOR)
        )

        val wirtschaftsingenieurwesenProfs = listOf(
            User(userName = "Prof. Ute Diemar", role = CREATOR),
            User(userName = "Prof. Michael Engler", role = CREATOR),
            User(userName = "Prof. Hans-Georg Enkler", role = CREATOR),
            User(userName = "Prof. Michael Gehrer", role = CREATOR),
            User(userName = "Prof. Katja Gutsche", role = CREATOR),
            User(userName = "Prof. Jörg Jacobi", role = CREATOR),
            User(userName = "Prof. Steffen Jäger", role = CREATOR),
            User(userName = "Prof. Ulrich Kallmann", role = CREATOR),
            User(userName = "Prof. Hartmut Katz", role = CREATOR),
            User(userName = "Prof. Uwe Kenntner", role = CREATOR),
            User(userName = "Prof. Gerhard Kirchner", role = CREATOR),
            User(userName = "Prof. Harald Kopp", role = CREATOR),
            User(userName = "Prof. Christian Krause", role = CREATOR),
            User(userName = "Prof. Lutz Leuendorf", role = CREATOR),
            User(userName = "Prof. Steffen Munk", role = CREATOR),
            User(userName = "Prof. Christa Pfeffer", role = CREATOR),
            User(userName = "Prof. Bernhard Plum", role = CREATOR),
            User(userName = "Prof. Robert Schäflein-Armbruster", role = CREATOR),
            User(userName = "Prof. Christoph Uhrhan", role = CREATOR),
            User(userName = "Prof. Christian van Husen", role = CREATOR)
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
        ).flatten().map { it.copy(password = "1234".generateHash()) }
    }

    private fun generateSubjects() {
        val dmMibSubjects = listOf(
            MongoSubject(abbreviation = "MIB", name = "Allgemeine Betriebswirtschaftslehre"),
            MongoSubject(abbreviation = "AudioTech", name = "Audiotechnik"),
            MongoSubject(abbreviation = "GrdBetr.", name = "Grundlagen der Betriebswirtschaftslehre"),
            MongoSubject(abbreviation = "GrdUnGr.", name = "Grundlagen der Unternehmensgründung"),
            MongoSubject(abbreviation = "GrMeGe", name = "Grundlagen Mediengestaltung"),
            MongoSubject(abbreviation = "MatMedInf", name = "Mathematik in Medien und Informatik"),
            MongoSubject(abbreviation = "MedPsych", name = "Medienpsychologie"),
            MongoSubject(abbreviation = "MedTech", name = "Medientechnik"),
            MongoSubject(abbreviation = "MINT", name = "MINT-Grundlagen"),
            MongoSubject(abbreviation = "PhMedInf", name = "Physik in Medien und Informatik"),
            MongoSubject(abbreviation = "Prog", name = "Programmierung"),
            MongoSubject(abbreviation = "VideoTech", name = "Videotechnik"),
            MongoSubject(abbreviation = "ComGra", name = "Computergrafik"),
            MongoSubject(abbreviation = "GIS", name = "Grundlagen Interaktiver Systeme"),
            MongoSubject(abbreviation = "Marketing", name = "Marketing"),
            MongoSubject(abbreviation = "MathSimSem", name = "Mathematik und Simulation"),
            MongoSubject(abbreviation = "Medienökonomie", name = "Medienökonomie"),
            MongoSubject(abbreviation = "UsExDe", name = "User Experience Design"),
            MongoSubject(abbreviation = "GrafDV", name = "Grafische Datenverarbeitung"),
            MongoSubject(abbreviation = "KoSy", name = "Kommunikationssysteme"),
            MongoSubject(abbreviation = "ProManSoSk", name = "Projektmanagement und Soft Skills"),
            MongoSubject(abbreviation = "SWDes", name = "Softwaredesign"),
            MongoSubject(abbreviation = "WiArSchr", name = "Wissenschaftliches Arbeiten und Schreiben"),
            MongoSubject(abbreviation = "DaVerMe", name = "Datenverarbeitung in der Medienproduktion"),
            MongoSubject(abbreviation = "DigAvTech", name = "Digitale AV - Technik"),
            MongoSubject(abbreviation = "verAnw", name = "Verteilte Anwendungen"),
            MongoSubject(abbreviation = "ProjStud", name = "Projektstudium"),
            MongoSubject(abbreviation = "ItOnlPm", name = "IT- und Medien-Produktmanagement"),
            MongoSubject(abbreviation = "ManavMedien", name = "Management von Medienprodukten")
        )

        val wiIbsSubjects = listOf(
            MongoSubject(abbreviation = "AppMath", name = "Applied Mathematics"),
            MongoSubject(abbreviation = "BusMan", name = "Business Management"),
            MongoSubject(abbreviation = "PrSkCaDev", name = "Professional Skills & Career Development"),
            MongoSubject(abbreviation = "ProFun", name = "Programming Fundamentals"),
            MongoSubject(abbreviation = "-", name = "Scientific Working / Academic Writing"),
            MongoSubject(abbreviation = "-", name = "Systems and Network Architectures"),
            MongoSubject(abbreviation = "AppSta", name = "Applied Statistics"),
            MongoSubject(abbreviation = "BusPro", name = "Business Processes"),
            MongoSubject(abbreviation = "BatBas", name = "Databases"),
            MongoSubject(abbreviation = "DigMarECo", name = "Digital Marketing and eCommerce"),
            MongoSubject(abbreviation = "FinAcc", name = "Financial Accounting"),
            MongoSubject(abbreviation = "ObOrProg", name = "Object-oriented Programming"),
            MongoSubject(abbreviation = "AppArIn", name = "Applied Artificial Intelligence"),
            MongoSubject(abbreviation = "OpLog", name = "Operations and Logistics"),
            MongoSubject(abbreviation = "EnWir", name = "Energiewirtschaft"),
            MongoSubject(abbreviation = "InnovddWe", name = "Innovationen die die Welt verändern"),
            MongoSubject(abbreviation = "BPOPMRC", name = "Business Process Optimization - Process Mining"),
            MongoSubject(abbreviation = "BWL", name = "Computer Security"),
            MongoSubject(abbreviation = "BWL", name = "ERP-Systems"),
            MongoSubject(abbreviation = "BWL", name = "Introduction to Internship"),
            MongoSubject(abbreviation = "BWL", name = "Management Accounting and Controlling"),
            MongoSubject(abbreviation = "BWL", name = "Project Management"),
            MongoSubject(abbreviation = "BWL", name = "Web-based Technologies"),
        )

        val wiWibSubjects = listOf(
            MongoSubject(abbreviation = "BWL", name = "Betriebswirtschaftslehre"),
            MongoSubject(abbreviation = "DB", name = "Datenbanken"),
            MongoSubject(abbreviation = "EinfWInfor", name = "Einführung in die Wirtschaftsinformatik"),
            MongoSubject(abbreviation = "-", name = "Einführung in Wissenschaftliches Arbeiten"),
            MongoSubject(abbreviation = "Prog1", name = "Programmieren 1"),
            MongoSubject(abbreviation = "WIMatheSta", name = "Wirtschaftsmathematik und -statistik"),
            MongoSubject(abbreviation = "GeProDes", name = "Geschäftsprozessdesign"),
            MongoSubject(abbreviation = "Prog2", name = "Programmieren 2"),
            MongoSubject(abbreviation = "RWContr", name = "Rechnungswesen und Controlling"),
            MongoSubject(abbreviation = "RhPrMoTech", name = "Rhetorik, Präsentations- und Moderationstechnik"),
            MongoSubject(abbreviation = "SysNwAr", name = "System- und Netzwerkarchitekturen"),
            MongoSubject(abbreviation = "FoMeDa", name = "Formale Methoden und Datenstrukturen"),
            MongoSubject(abbreviation = "InStaSW", name = "Integrierte Standard-Software"),
            MongoSubject(abbreviation = "IntWor", name = "Internetworking"),
            MongoSubject(abbreviation = "LoSuChMa", name = "Logistik und Supply Chain Management "),
            MongoSubject(abbreviation = "SWEng", name = "Software - Engineering"),
            MongoSubject(abbreviation = "BusPro", name = "Business - Projekt"),
            MongoSubject(abbreviation = "BuIn", name = "Business Intelligence"),
            MongoSubject(abbreviation = "ITMan", name = "IT-Management"),
            MongoSubject(abbreviation = "MobSys", name = "Mobile Systeme"),
            MongoSubject(abbreviation = "VerSys", name = "Verteilte Systeme"),
            MongoSubject(abbreviation = "VWL", name = "Volkswirtschaftslehre"),
            MongoSubject(abbreviation = "SW-Project", name = "Software-Projekt")
        )
    }

}