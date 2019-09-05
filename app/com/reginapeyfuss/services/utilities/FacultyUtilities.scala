package com.reginapeyfuss.services.utilities

import com.reginapeyfuss.services.faculty._

object FacultyUtilities {
    type YYYYMM = Int

    def parseFacultyType(name: String): FacultyType = {
        name match {
            case "Administrator" => Administrator
            case "Teacher" => Teacher
            case "SupportStaff" => SupportStaff
            case _ => Invalid
        }
    }
}
