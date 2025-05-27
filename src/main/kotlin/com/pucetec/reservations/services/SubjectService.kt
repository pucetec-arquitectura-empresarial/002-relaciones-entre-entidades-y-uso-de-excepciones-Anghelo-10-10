package com.pucetec.reservations.services

import com.pucetec.reservations.exceptions.ProfessorNotFoundException
import com.pucetec.reservations.exceptions.StudentAlreadyEnrolledException
import com.pucetec.reservations.exceptions.StudentNotFoundException
import com.pucetec.reservations.exceptions.SubjectNotFoundException
import com.pucetec.reservations.mappers.SubjectMapper
import com.pucetec.reservations.models.entities.Subject
import com.pucetec.reservations.models.requests.SubjectRequest
import com.pucetec.reservations.models.responses.SubjectResponse
import com.pucetec.reservations.repositories.ProfessorRepository
import com.pucetec.reservations.repositories.StudentRepository
import com.pucetec.reservations.repositories.SubjectRepository
import org.springframework.stereotype.Service

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository,
    private val subjectMapper: SubjectMapper,
) {
    fun createSubject(request: SubjectRequest): SubjectResponse {

        val professor = professorRepository.findById(request.professorId)
            .orElseThrow { ProfessorNotFoundException("Professor with ID ${request.professorId} not found") }
        print(professor)

        val subject = Subject(
            name = request.name,
            semester = request.semester,
            professor = professor,
        )


        val savedSubject = subjectRepository.save(subject)


        return subjectMapper.toResponse(savedSubject)
    }

    fun enrollStudent(subjectId: Long, studentId: Long): SubjectResponse {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { SubjectNotFoundException("Subject with ID $subjectId not found") }


        val student = studentRepository.findById(studentId)
            .orElseThrow { StudentNotFoundException("Student with ID $studentId not found") }


        if (subject.students.contains(student)) {
            throw StudentAlreadyEnrolledException("Student with ID $studentId is already enrolled in subject with ID $subjectId")
        }


        subject.students.add(student)
        val updatedSubject = subjectRepository.save(subject)


        return subjectMapper.toResponse(updatedSubject)
    }

    fun getSubjectById(subjectId: Long): SubjectResponse {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { SubjectNotFoundException("Subject with ID $subjectId not found") }

        return subjectMapper.toResponse(subject)
    }

    fun deleteSubject(subjectId: Long) {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { SubjectNotFoundException("Subject with ID $subjectId not found") }

        subjectRepository.delete(subject)
    }

    fun listSubjects(): List<SubjectResponse> =
        subjectMapper.toResponseList(subjectRepository.findAll())
}