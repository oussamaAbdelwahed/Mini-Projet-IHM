package spring.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.blog.models.ContactMessage;


@Repository
public interface ContactMessageRepository  extends JpaRepository<ContactMessage, Long>{

}
