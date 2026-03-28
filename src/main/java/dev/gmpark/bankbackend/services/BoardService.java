package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.entities.BoardEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.mappers.BoardMapper;
import dev.gmpark.bankbackend.mappers.UserMapper;
import dev.gmpark.bankbackend.results.BoardResult;
import dev.gmpark.bankbackend.vos.BoardPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;

    public BoardResult write(BoardEntity board, UserEntity user) {
        if(board.getBoardType() == null || board.getTitle() == null || board.getContent() == null)
        {
            return BoardResult.FAILURE;
        }
        if( !(board.getBoardType().equals("notice") || board.getBoardType().equals("event"))) {
            return BoardResult.FAILURE_INVALIDATE_BOARD_TYPE;
        }
        board.setBoardType(board.getBoardType());
        board.setTitle(board.getTitle());
        board.setContent(board.getContent());
        board.setUserId(user.getId());
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());
        int result = this.boardMapper.insert(board);

        return result > 0 ? BoardResult.SUCCESS : BoardResult.FAILURE;
    }
    public BoardResult modify( BoardEntity board, UserEntity user) {
        if( user == null) {
            return BoardResult.FAILURE;
        }
        if( board == null || board.getBoardId() < 1) {
            return BoardResult.FAILURE;
        }
        BoardEntity dbBoard = this.boardMapper.selectById(board.getBoardId());
        if( dbBoard == null) {
            return BoardResult.FAILURE;
        }
        if( !dbBoard.getUserId().equals(user.getId())) {
            return BoardResult.FAILURE;
        }
        dbBoard.setTitle(board.getTitle());
        dbBoard.setContent(board.getContent());
        dbBoard.setUpdatedAt(LocalDateTime.now());
        int result = this.boardMapper.update(dbBoard);
        return result > 0 ? BoardResult.SUCCESS : BoardResult.FAILURE;
        
    }


    public BoardResult delete(Integer boardId, UserEntity user) {
        if (user == null) {
            return BoardResult.FAILURE; // 로그인 안됨
        }
        if (boardId == null || boardId < 1) {
            return BoardResult.FAILURE; // 유효하지 않은 게시글 ID
        }
        
        BoardEntity dbBoard = this.boardMapper.selectById(boardId);
        if (dbBoard == null) {
            return BoardResult.FAILURE; // 게시글 존재하지 않음
        }
        
        if (!dbBoard.getUserId().equals(user.getId())) {
            return BoardResult.FAILURE; // 삭제 권한 없음 (본인 글 아님)
        }
        
        int result = this.boardMapper.delete(boardId);
        return result > 0 ? BoardResult.SUCCESS : BoardResult.FAILURE;
    }

    public BoardEntity getArticleByBoardId(Integer boardId) {
        if (boardId == null || boardId < 1) {
            return null;
        }
        BoardEntity board = this.boardMapper.selectById(boardId);
        if (board != null) {
            // 게시글 조회 시 조회수(view_count) 1 증가
            board.setViewCount(board.getViewCount() + 1);
            this.boardMapper.update(board);
        }
        return board;
    }
    
    public Pair<BoardPageVo, List<BoardEntity>> getList(String boardType, int requestPage) {
        if (boardType == null || (!boardType.equals("notice") && !boardType.equals("event"))) {
            return null; // 잘못된 boardType
        }
        
        int totalCount = this.boardMapper.selectCountByBoardType(boardType);
        BoardPageVo pageVo = new BoardPageVo(requestPage, totalCount);
        
        List<BoardEntity> articles = this.boardMapper.selectArticlesByBoardType(boardType, pageVo.getRowCount(), pageVo.getDbOffset());
        
        return Pair.of(pageVo, articles);
    }
    public List<BoardEntity> getFourArticles(String boardType) {
        if (boardType == null || (!boardType.equals("notice") && !boardType.equals("event"))) {
            return null;
        }
        return this.boardMapper.selectFourArticlesByBoardType(boardType);
    }
}
