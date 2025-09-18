package gov.uk.ets.registry.api.itl.message.service;

import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.itl.message.ITLMessageService;
import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.repository.ITLMessageRepository;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSendResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ITLMessageManagementService {

	private final ITLMessageService messageService;
	private final ITLMessageRepository messageRepository;

	public Page<AcceptMessageLog> search(@Valid ITLMessageSearchCriteria criteria, Pageable pageable) {
		return messageRepository.search(criteria, pageable);
	}

	public Optional<AcceptMessageLog> getMessage(Long messageId) {
		return messageRepository.findById(messageId);
	}

    @Transactional
	public ITLMessageSendResponse sendMessage(String messageContent) {
		AcceptMessageLog acceptMessageLog =messageService.sendMessage(messageContent);
		return ITLMessageSendResponse.builder().success(true).messageId(acceptMessageLog.getId()).build();
	}
}
