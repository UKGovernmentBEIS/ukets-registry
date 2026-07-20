package gov.uk.ets.registry.api.itl.message.service;

import java.util.Optional;

import jakarta.validation.Valid;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.repository.ITLMessageRepository;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSendResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ITLMessageManagementService {

	private final ITLMessageRepository messageRepository;

	public Page<AcceptMessageLog> search(@Valid ITLMessageSearchCriteria criteria, Pageable pageable) {
		return messageRepository.search(criteria, pageable);
	}

	public Optional<AcceptMessageLog> getMessage(Long messageId) {
		return messageRepository.findById(messageId);
	}

    @Transactional
    @Deprecated(forRemoval = true, since = "v5.32.0")
	public ITLMessageSendResponse sendMessage(String messageContent) {
    	throw new InvalidOperationException("ITL is disconnected.");
	}
}
