package jje.happy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jje.happy.mapper.BoardAttachMapper;
import jje.happy.mapper.BoardMapper;
import jje.happy.vo.BoardAttachVO;
import jje.happy.vo.BoardVO;
import jje.happy.vo.Criteria;
import jje.happy.vo.MemberVO;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service // 비즈니스 영역을 담당하고 있음을 명시
@AllArgsConstructor
public class BoardServiceImpl implements BoardService {

	@Setter(onMethod_ = @Autowired)
	private BoardMapper mapper;
	
	@Setter(onMethod_ = @Autowired)
	private BoardAttachMapper attachMapper;
	
	 @Setter(onMethod_ = @Autowired)
	  private PasswordEncoder pwencoder;

	@Transactional
	@Override
	public void register(BoardVO board) {

		log.info("register......" + board);

		mapper.insertSelectKey(board);

		if (board.getAttachList() == null || board.getAttachList().size() <= 0) {
			return;
		}

		board.getAttachList().forEach(attach -> {

			attach.setBno(board.getBno());
			attachMapper.insert(attach);
		});
	}

	@Override
	public BoardVO get(Long bno) {

		log.info("get......" + bno);

		return mapper.read(bno);

	}

	@Transactional
	@Override
	public boolean modify(BoardVO board) {

		log.info("modify......" + board);

		attachMapper.deleteAll(board.getBno());

		boolean modifyResult = mapper.update(board) == 1;
		
		if (modifyResult && board.getAttachList().size() > 0) {

			board.getAttachList().forEach(attach -> {

				attach.setBno(board.getBno());
				attachMapper.insert(attach);
			});
		}

		return modifyResult;
	}

	@Transactional
	@Override
	public boolean remove(Long bno) {

		log.info("remove...." + bno);
		
		attachMapper.deleteAll(bno);

		return mapper.delete(bno) == 1;
	}

	@Override
	public List<BoardVO> getList(Criteria cri) {

		log.info("get List with criteria: " + cri);

		return mapper.getListWithPaging(cri);
	}

	@Override public int getTotal(Criteria cri) {
		  
		  log.info("get total count"); 
		  return mapper.getTotalCount(cri); 
		  }
	

	@Override
	public List<BoardAttachVO> getAttachList(long bno) {
		log.info("get Attach list by bno" + bno);

		return attachMapper.findByBno(bno);
	}

	@Transactional
	@Override
	public void sign(MemberVO mem) {
		mapper.insert(mem);
		mapper.insertAuth(mem);
	}

	@Override
	public int idCheck(MemberVO mem) {
		
		mem.setUserpw(pwencoder.encode(mem.getUserpw()));
		
		if(mapper.idCheck(mem) != null) {
			
			return 0;
		}else {
			return 1;
		}
		
	}

	@Override
	public List<BoardVO> getListScore(Criteria cri) {
		// TODO Auto-generated method stub
		log.info("get List with criteria: " + cri);

		return mapper.getListWithPagingScore(cri);
	}

	@Override
	public List<BoardVO> getListReplyCnt(Criteria cri) {
		// TODO Auto-generated method stub
		log.info("get List with criteria: " + cri);

		return mapper.getListWithPagingReplyCnt(cri);
	}

}
