/*|-----------------------------------------------------------------------------
 *|            This source code is provided under the Apache 2.0 license      --
 *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
 *|                See the project's LICENSE.md for details.                  --
 *|           Copyright Thomson Reuters 2016. All rights reserved.            --
 *|-----------------------------------------------------------------------------
*/

#ifndef __thomsonreuters_ema_access_DirectoryHandler_h
#define __thomsonreuters_ema_access_DirectoryHandler_h

#include "rtr/rsslReactor.h"
#include "EmaVector.h"

namespace thomsonreuters {

namespace ema {

namespace access {

class OmmServerBaseImpl;
class EmaConfigServerImpl;
class EmaString;
class ItemInfo;

class DirectoryHandler
{
public:

	static RsslReactorCallbackRet directoryCallback(RsslReactor*, RsslReactorChannel*, RsslRDMDirectoryMsgEvent*);

	static DirectoryHandler* create(OmmServerBaseImpl*);

	static void destroy(DirectoryHandler*&);

	void initialize(EmaConfigServerImpl*);

	const EmaVector< ItemInfo* >&		getDirectoryItemList();

	void addItemInfo(ItemInfo*);

	void removeItemInfo(ItemInfo*);

private:

	static const EmaString			_clientName;

	OmmServerBaseImpl*			_pOmmServerBaseImpl;
	bool						_apiAdminControl;
	RsslBuffer					_rsslMsgBuffer;
	RsslEncIterator				_rsslEncodeIter;
	RsslDecIterator				_rsslDecodeIter;
	RsslRDMDirectoryMsg			_rsslRdmDirectoryMsg;
	RsslBuffer					_refreshText;

	EmaVector< ItemInfo* >		_itemInfoList;

	DirectoryHandler(OmmServerBaseImpl*);
	virtual ~DirectoryHandler();

	void handleDirectoryRequest(RsslReactorChannel*, RsslRDMDirectoryRequest&);
	void sendDirectoryReject(RsslReactorChannel*, RsslInt32, RsslStateCodes, EmaString&);

	void notifyOnClose(RsslMsg*, ItemInfo*);

	DirectoryHandler();
	DirectoryHandler(const DirectoryHandler&);
	DirectoryHandler& operator=(const DirectoryHandler&);
};

}

}

}

#endif // __thomsonreuters_ema_access_DirectoryHandler_h

