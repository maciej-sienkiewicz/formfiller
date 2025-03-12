package pl.potentiai.formfiller.infrastructure.sms

import pl.potentiai.formfiller.domain.sms.SMSTemplate
import pl.potentiai.formfiller.domain.sms.SmsTemplateRepository

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSmsTemplateRepository : JpaRepository<SMSTemplateEntity, Long>

class PostgresSMSRepository(private val smsTemplateRepository: JpaSmsTemplateRepository): SmsTemplateRepository {

    override fun getTemplate(templateId: Long): SMSTemplate? =
        smsTemplateRepository.findById(templateId)
            .orElse(null)
            ?.toDomainObject()

    override fun getAll(): List<SMSTemplate> =
        smsTemplateRepository.findAll()
            .map { it.toDomainObject() }

    private fun SMSTemplateEntity.toDomainObject(): SMSTemplate = SMSTemplate(
        id = this.id,
        name = this.name,
        content = this.content
    )
}

@Entity
@Table(name = "sms_templates")
class SMSTemplateEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    lateinit var name: String

    @Column(nullable = false, columnDefinition = "TEXT")
    lateinit var content: String

    constructor(name: String, content: String) : this() {
        this.name = name
        this.content = content
    }
}
