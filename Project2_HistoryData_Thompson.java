//
//  HistoryData.java
//  ChessApp
//
//  Created by Peter Hunter on Mon Dec 31 2001.
//  Copyright (c) 2001 Peter Hunter. All rights reserved.
//

/**
 * History data object.
 * 
 * @modified Kurtis Thompson:
 * 
 * I wouldn't have made this a class but the original author did.
 * The object are used to keep track of our move history for principal variation as well as the ability to undo the move.
 * 
 * 
 */

final class Project2_HistoryData_Thompson {
	Project2_Move_Thompson m;
    int capture;
    int castle;
    int ep;
    int fifty;
}
