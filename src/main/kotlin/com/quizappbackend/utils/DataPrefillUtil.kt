package com.quizappbackend.utils

import com.quizappbackend.model.databases.Degree.*
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoCourseOfStudies
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoFaculty
import com.quizappbackend.model.databases.mongodb.documents.faculty.MongoSubject
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoAnswer
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestion
import com.quizappbackend.model.databases.mongodb.documents.questionnaire.MongoQuestionnaire
import com.quizappbackend.model.databases.mongodb.documents.user.AuthorInfo
import org.litote.kmongo.div

object DataPrefillUtil {

    fun generateFacultiesAndCoursesOfStudies(): Pair<List<MongoFaculty>, List<MongoCourseOfStudies>> {
        val dm = MongoFaculty(abbreviation = "DM", name = "Digitale Medien")

        val dmMIB = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "MIB", name = "Medieninformatik", degree = BACHELOR)
        val dmOMB = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "OMB", name = "Online Medien", degree = BACHELOR)
        val dmMKB = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "MKB", name = "Medienkonzeption", degree = BACHELOR)
        val dmMUM = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "MUM", name = "Music Design", degree = MASTER)
        val dmDIM = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "DIM", name = "Design Interaktiver Medien", degree = MASTER)
        val dmMIM = MongoCourseOfStudies(facultyIds = listOf(dm.id), abbreviation = "MIM", name = "Medieninformatik", degree = MASTER)
        val dmCosList = listOf(dmMIB, dmOMB, dmMKB, dmMUM, dmDIM, dmMIM)


        val gsg = MongoFaculty(abbreviation = "GSG", name = "Gesundheit, Sicherheit, Gesellschaft")

        val gsgAGW = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "AGW", name = "Angewandte Gesundheitswissenschaften", degree = BACHELOR)
        val gsgHW = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "HW", name = "Hebammenwissenschaft", degree = BACHELOR)
        val gsgPT = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "PT", name = "Physiotherapie", degree = BACHELOR)
        val gsgSSB = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "SSB", name = "Security and Safety Engineering", degree = BACHELOR)
        val gsgAGF = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "AGF", name = "Angewandte Gesundheitsförderung", degree = MASTER)
        val gsgIGF = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "IGF", name = "Interdisziplinäre Gesundheitsförderung", degree = MASTER)
        val gsgRIW = MongoCourseOfStudies(facultyIds = listOf(gsg.id), abbreviation = "RIW", name = "Risikoingenieurwesen", degree = MASTER)
        val gsgCosList = listOf(gsgAGW, gsgHW, gsgPT, gsgSSB, gsgAGF, gsgIGF, gsgRIW)


        val in_ = MongoFaculty(abbreviation = "IN", name = "Informatik")

        val inAIN = MongoCourseOfStudies(facultyIds = listOf(in_.id), abbreviation = "AIN", name = "Allgemeine Informatik", degree = BACHELOR)
        val inITP = MongoCourseOfStudies(facultyIds = listOf(in_.id), abbreviation = "ITP", name = "IT-Produktmanagement", degree = BACHELOR)
        val inSPB = MongoCourseOfStudies(facultyIds = listOf(in_.id), abbreviation = "SPB", name = "Software Produktmanagement", degree = BACHELOR)
        val inINM = MongoCourseOfStudies(facultyIds = listOf(in_.id), abbreviation = "INM", name = "Informatik", degree = MASTER)
        val inMOS = MongoCourseOfStudies(facultyIds = listOf(in_.id), abbreviation = "MOS", name = "Mobile Systeme", degree = MASTER)
        val inCosList = listOf(inAIN, inITP, inSPB, inINM, inMOS)


        val ite = MongoFaculty(abbreviation = "ITE", name = "Industrial Technologies")

        val iteOT = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "OT", name = "Studienmodell Orientierung Technik", degree = BACHELOR)
        val iteIMT = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "IMT", name = "Industrial MedTec", degree = BACHELOR)
        val iteIAM = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "IAM", name = "Industrial Automation and Mechatronics", degree = BACHELOR)
        val iteISD = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "ISD", name = "Industrial Systems Design", degree = BACHELOR)
        val iteIMF = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "IMF", name = "Industrial Manufactoring", degree = BACHELOR)
        val iteIME = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "IME", name = "Industrial Materials Engineering", degree = BACHELOR)
        val iteIP = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "IP", name = "Ingenieurpsychologie", degree = BACHELOR)
        val iteMDP = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "MDP", name = "Mechatronik und Digitale Produktion", degree = BACHELOR)
        val iteMTE = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "MTE", name = "Medizintechnik - Technologien und Entwicklungsprozesse", degree = BACHELOR)
        val iteWFT = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "WFT", name = "Werkstoff- und Fertigungstechnik", degree = BACHELOR)
        val iteAMW = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "AMW", name = "Angewandte Materialwissenschaften", degree = MASTER)
        val iteHF = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "HF", name = "Human Factors", degree = MASTER)
        val iteMES = MongoCourseOfStudies(facultyIds = listOf(ite.id), abbreviation = "MES", name = "Mechatronische Systeme", degree = MASTER)
        val iteCosList = listOf(iteOT, iteIMT, iteIAM, iteISD, iteIMF, iteIME, iteIP, iteMDP, iteMTE, iteWFT, iteAMW, iteHF, iteMES)


        val mls = MongoFaculty(abbreviation = "MLS", name = "Medical and Life Sciences")

        val mlsANB = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "ANB", name = "Angewandte Biologie", degree = BACHELOR)
        val mlsBPT = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "BPT", name = "Bio- und Prozesstechnologie", degree = BACHELOR)
        val mlsMTZ = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "MTZ", name = "Molekulare und Technische Medizin", degree = BACHELOR)
        val mlsNBT = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "NBT", name = "Nachhaltige Bioprozesstechnik", degree = MASTER)
        val mlsMDT = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "MDT", name = "Medical Diagnostic Technologies", degree = MASTER)
        val mlsPMD = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "PMD", name = "Precision Medicine Diagnostics", degree = MASTER)
        val mlsTP = MongoCourseOfStudies(facultyIds = listOf(mls.id), abbreviation = "TP", name = "Technical Physician", degree = MASTER)
        val mlsCosList = listOf(mlsANB, mlsBPT, mlsMTZ, mlsNBT, mlsMDT, mlsPMD, mlsTP)


        val mme = MongoFaculty(abbreviation = "MME", name = "Mechanical and Medical Engineering")

        val mmeELA = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "ELA", name = "Elektrotechnik in Anwendung", degree = BACHELOR)
        val mmeMM = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "MM", name = "Maschinenbau und Mechatronik", degree = BACHELOR)
        val mmeMEB = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "MEB", name = "Medical Engineering", degree = BACHELOR)
        val mmeMKT = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "MKT", name = "Medizintechnik - Klinische Technologien", degree = BACHELOR)
        val mmeICS = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "ICS", name = "Information Communication Systems", degree = BACHELOR)
        val mmeAPE = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "APE", name = "Advanced Precision Engineering", degree = MASTER)
        val mmeBME = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "BME", name = "Biomedical Engineering", degree = MASTER)
        val mmeMZT = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "MZT", name = "Mikromedizin", degree = MASTER)
        val mmePMM = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "PMM", name = "Precision Manufactoring and Management", degree = MASTER)
        val mmeSMA = MongoCourseOfStudies(facultyIds = listOf(mme.id), abbreviation = "SMA", name = "Smart Systems", degree = MASTER)
        val mmeCosList = listOf(mmeELA, mmeMM, mmeMEB, mmeMKT, mmeICS, mmeAPE, mmeBME, mmeMZT, mmePMM, mmeSMA)


        val w = MongoFaculty(abbreviation = "W", name = "Wirtschaft")

        val wBMP = MongoCourseOfStudies(facultyIds = listOf(w.id), abbreviation = "BMP", name = "Business Management and Psychology", degree = BACHELOR)
        val wIBW = MongoCourseOfStudies(facultyIds = listOf(w.id), abbreviation = "IBW", name = "Internationale Betriebswirtschaft", degree = BACHELOR)
        val wIBM = MongoCourseOfStudies(facultyIds = listOf(w.id), abbreviation = "IBM", name = "International Business Management", degree = BACHELOR)
        val wIMM = MongoCourseOfStudies(facultyIds = listOf(w.id), abbreviation = "IMM", name = "International Management", degree = MASTER)
        val wMBA = MongoCourseOfStudies(facultyIds = listOf(w.id), abbreviation = "MBA", name = "International Business Management", degree = MASTER)
        val wCosList = listOf(wBMP, wIBW, wIBM, wIMM, wMBA)


        val mme_w_IEB = MongoCourseOfStudies(facultyIds = listOf(mme.id, w.id), abbreviation = "IEB", name = "International Engineering", degree = BACHELOR)
        val mme_w_CosList = listOf(mme_w_IEB)


        val wi = MongoFaculty(abbreviation = "WI", name = "Wirtschaftsinformatik")

        val wiIBS = MongoCourseOfStudies(facultyIds = listOf(wi.id), abbreviation = "IBS", name = "International Business Information Systems", degree = BACHELOR)
        val wiWIB = MongoCourseOfStudies(facultyIds = listOf(wi.id), abbreviation = "WIB", name = "Wirtschaftsinformatik", degree = BACHELOR)
        val wiWNB = MongoCourseOfStudies(facultyIds = listOf(wi.id), abbreviation = "WNB", name = "Wirtschaftsnetze eBusiness", degree = BACHELOR)
        val wiBAM = MongoCourseOfStudies(facultyIds = listOf(wi.id), abbreviation = "BAM", name = "Business Application Architecture", degree = MASTER)
        val wiBCM = MongoCourseOfStudies(facultyIds = listOf(wi.id), abbreviation = "BCM", name = "Business Consulting", degree = MASTER)
        val wiCosList = listOf(wiIBS, wiWIB, wiWNB, wiBAM, wiBCM)


        val wing = MongoFaculty(abbreviation = "WING", name = "Wirtschaftsingenieurwesen")

        val wingWIS = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "WIS", name = "Industrial Solutions Management", degree = BACHELOR)
        val wingSMB = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "SMB", name = "Service Management", degree = BACHELOR)
        val wingMVB = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "MVB", name = "Marketing und Vertrieb", degree = BACHELOR)
        val wingPEB = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "PEB", name = "Product Engineering", degree = BACHELOR)
        val wingWPI = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "WPI", name = "Product Innovation", degree = MASTER)
        val wingSEM = MongoCourseOfStudies(facultyIds = listOf(wing.id), abbreviation = "SEM", name = "Sales & Service Engineering", degree = MASTER)
        val wingCosList = listOf(wingWIS, wingSMB, wingMVB, wingPEB, wingWPI, wingSEM)


        val faculties = listOf(dm, gsg, in_, ite, mls, mme, w, wi, wing)

        val coursesOfStudies = listOf(
            dmCosList, gsgCosList, inCosList, iteCosList,
            mlsCosList, mmeCosList, wCosList, mme_w_CosList, wiCosList, wingCosList
        ).flatten()

        return Pair(faculties, coursesOfStudies)
    }


    private fun generateSubjects(){
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